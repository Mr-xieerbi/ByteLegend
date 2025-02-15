/*
 * Copyright 2021 ByteLegend Technologies and the original author or authors.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/ByteLegend/ByteLegend/blob/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bytelegend.app.shared.entities.mission

import com.bytelegend.app.shared.annotations.JsonCreator
import com.bytelegend.app.shared.i18n.Locale

/**
 * Represent the data to display at tutorial tab
 */
data class Tutorial constructor(
    val id: String,
    val title: String,
    val type: String,
    val href: String,
    val languages: List<Locale>,
    val content: String = ""
) {
    @JsonCreator
    constructor(
        id: String,
        title: String,
        type: String,
        href: String,
        language: Locale?,
        languages: List<Locale>?,
        content: String = ""
    ) : this(id, title, type, href, languages ?: listOf(language!!), content)
}
