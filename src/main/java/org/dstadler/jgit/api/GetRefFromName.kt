package org.dstadler.jgit.api

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import java.io.IOException

/*
   Copyright 2013, 2014 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */ /**
 * Simple snippet which shows how to retrieve a Ref for some reference string.
 */
object GetRefFromName {

	@JvmStatic
	@Throws(IOException::class)
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			// the Ref holds an ObjectId for any type of object (tree, commit, blob, tree)
			val head = repository.exactRef("refs/heads/master")
			println("Ref of refs/heads/master: " + head + ": " + head.name + " - " + head.objectId.name)
		}
	}
}