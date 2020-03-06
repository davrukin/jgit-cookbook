package org.dstadler.jgit.api

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.revwalk.RevWalk
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
 * Simple snippet which shows how to use RevWalk to read tags
 */
object ReadTagFromName {

	@Throws(IOException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			RevWalk(repository).use { walk ->
				// a simple tag that is not annotated
				val simpleTag = repository.findRef("initialtag")
				var any = walk.parseAny(simpleTag.objectId)
				println("Commit: $any")

				// an annotated tag
				val annotatedTag = repository.findRef("secondtag")
				any = walk.parseAny(annotatedTag.objectId)
				println("Tag: $any")

				// finally try to print out the tag-content
				println("\nTag-Content: \n")
				val loader = repository.open(annotatedTag.objectId)
				loader.copyTo(System.out)
				walk.dispose()
			}
		}
	}
}