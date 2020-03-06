package org.dstadler.jgit.porcelain

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
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
 * Simple snippet which shows how to list all Tags
 *
 * @author dominik.stadler at gmx.at
 */
object ListTags {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			Git(repository).use { git ->
				val call = git.tagList().call()
				for (ref in call) {
					println("Tag: " + ref + " " + ref.name + " " + ref.objectId.name)

					// fetch all commits for this tag
					val log = git.log()
					val peeledRef = repository.refDatabase.peel(ref)
					if (peeledRef.peeledObjectId != null) {
						log.add(peeledRef.peeledObjectId)
					} else {
						log.add(ref.objectId)
					}
					val logs = log.call()
					for (rev in logs) {
						println("Commit: $rev" /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */)
					}
				}
			}
		}
	}
}