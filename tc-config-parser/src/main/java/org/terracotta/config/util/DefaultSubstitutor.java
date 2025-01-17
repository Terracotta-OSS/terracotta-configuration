/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terracotta.config.util;

import org.terracotta.config.TCConfigurationSetupException;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * Applies defaults present in the schema. I don't suspect this will practically ever matter, but in theory there are a number of problems
 * with this code:
 * <ol>
 * <li>Is recursive -- can trip stack overflow
 * <li>Assumes no cycles in the graph -- I'm pretty sure this is true of JAXB object graphs
 * <li>Could cache reflective information about types instead of discovering on every walk
 * </ol>
 */

/*
 This is moved from the commons/config package for voltron.
 */
public class DefaultSubstitutor {

  private DefaultSubstitutor() {
    //
  }

  public static void applyDefaults(Object root) {
    Class<?> clazz = root.getClass();
    //don't process the internal types, these will be mostly service configuration which will be processed by specific
    //service.
    if (clazz.getAnnotation(XmlType.class) == null) {
      return;
    }
    for (Method method : clazz.getDeclaredMethods()) {
      if (method.getParameterTypes().length >= 1) {
        continue;
      }
      Object value = methodInvoke(root, method);
      if (value == null) {
        Field field = fieldFor(method);
        String def = getDefaultFor(field);
        if (def != null) {
          Type type = typeFor(method.getReturnType());
          if (type != null) {
            setField(root, field, type, def);
          } else {
            populateNonPrimitiveField(root, field, def);
          }
        }
      } else {
        if (value instanceof List) {
          for (Object e : ((List<?>) value)) {
            if (typeFor(e.getClass()) == null) {
              applyDefaults(e);
            }
          }
        } else if (typeFor(value.getClass()) == null) {
          applyDefaults(value);
        }
      }
    }
  }

  private static void populateNonPrimitiveField(Object obj, Field field, String def) {
    Class<?> fieldType = field.getType();

    Field valueField = valueFieldFor(fieldType);
    Type type = typeFor(valueField.getType());
    if (type == null) { throw new RuntimeException("Cannot handle non-primitive value field: " + valueField); }

    Object value = newInstanceWithDefaults(fieldType);

    field.setAccessible(true);
    valueField.setAccessible(true);
    try {
      valueField.set(value, type.valueFor(def));
      field.set(obj, value);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new TCConfigurationSetupException(e);
    }
  }


  private static <T> T newInstanceWithDefaults(Class<T> type) {
    T instance;
    try {
      instance = type.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    DefaultSubstitutor.applyDefaults(instance);
    return instance;
  }


  private static Field valueFieldFor(Class<?> type) {
    for (Field field : type.getDeclaredFields()) {
      XmlValue[] xmlValues = field.getDeclaredAnnotationsByType(XmlValue.class);
      if (xmlValues != null && xmlValues.length >= 1) { return field; }
    }

    throw new TCConfigurationSetupException("Expecting to find " + XmlValue.class.getName() + " present on a field of " + type);

  }

  private static void setField(Object obj, Field field, Type type, String value) {
    try {
      field.setAccessible(true);
      field.set(obj, type.valueFor(value));
    } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private static String getDefaultFor(Field field) {
    for (XmlElement xmlElement : field.getDeclaredAnnotationsByType(XmlElement.class)) {
      String def = xmlElement.defaultValue();
      if (def != null && !def.equals("\u0000")) { return def; }
    }
    return null;
  }

  private static Field fieldFor(Method method) {
    String fieldName;
    if (method.getReturnType() == Boolean.class) {
      fieldName = method.getName().replaceFirst("is", "");
    } else {
      fieldName = method.getName().replaceFirst("get", "");
    }

    fieldName = Introspector.decapitalize(fieldName);

    try {
      return method.getDeclaringClass().getDeclaredField(fieldName);
    } catch (NoSuchFieldException | SecurityException e) {
      throw new TCConfigurationSetupException(method.toString(), e);
    }
  }

  private static Object methodInvoke(Object obj, Method method) {
    if (method.getParameterTypes().length != 0) { throw new RuntimeException("Expecting zero arg method: " + method); }

    try {
      return method.invoke(obj);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new TCConfigurationSetupException(method.getDeclaringClass().getName() + ":" + method.getName(), e);
    }
  }

  private static Type typeFor(Class<?> type) {
    for (Type t : Type.values()) {
      if (t.matches(type)) { return t; }
    }

    return null;
  }

  private enum Type {

    BOOLEAN(Boolean.class, boolean.class) {
      @Override
      Object valueFor(String value) {
        return Boolean.valueOf(value);
      }
    },
    BYTE(Byte.class, byte.class) {
      @Override
      Object valueFor(String value) {
        return Byte.valueOf(value);
      }
    },
    CHARACTER(Character.class, char.class) {
      @Override
      Object valueFor(String value) {
        if (value.length() != 1) { throw new RuntimeException("invalid char: " + value); }

        return Character.valueOf(value.charAt(0));
      }
    },
    DOUBLE(Double.class, double.class) {
      @Override
      Object valueFor(String value) {
        return Double.valueOf(value);
      }
    },
    FLOAT(Float.class, float.class) {
      @Override
      Object valueFor(String value) {
        return Float.valueOf(value);
      }
    },
    INT(Integer.class, int.class) {
      @Override
      Object valueFor(String value) {
        return Integer.valueOf(value);
      }
    },
    LONG(Long.class, long.class) {
      @Override
      Object valueFor(String value) {
        return Long.valueOf(value);
      }
    },
    SHORT(Short.class, short.class) {
      @Override
      Object valueFor(String value) {
        return Short.valueOf(value);
      }
    },
    STRING(String.class, String.class) {
      @Override
      Object valueFor(String value) {
        return value;
      }
    };

    private final Class<?> type1;
    private final Class<?> type2;

    Type(Class<?> type1, Class<?> type2) {
      this.type1 = type1;
      this.type2 = type2;
    }

    abstract Object valueFor(String value);

    boolean matches(Class<?> t) {
      return type1 == t || type2 == t;
    }

  }

}
