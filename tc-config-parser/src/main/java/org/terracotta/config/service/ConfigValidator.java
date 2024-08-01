/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
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
package org.terracotta.config.service;

import org.w3c.dom.Element;

public interface ConfigValidator {
  /**
   * Validates equivalence of two xml fragments having same namespace
   *
   * @param oneFragment   is one of the xml fragment which is used in comparison
   * @param otherFragment is other xml fragment which is used in comparison
   * @throws {@link ValidationException} in case equivalence check fails.
   */
  default void validateAgainst(Element oneFragment, Element otherFragment) throws ValidationException {

  }

  /**
   * Checks given xml fragment is valid and acceptable for further operation
   *
   * @param fragment is the xml fragment which is validated
   * @throws {@link ValidationException} in case validation check fails.
   */
  default void validate(Element fragment) throws ValidationException {

  }

  /**
   * No-op implementation of {@link ConfigValidator}
   */
  ConfigValidator NOOP_VALIDATOR = new ConfigValidator() {

  };
}