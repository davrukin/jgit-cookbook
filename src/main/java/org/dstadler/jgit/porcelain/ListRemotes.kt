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
 * Snippet which shows how to iterate remotes, i.e. "git ls-remote"
 *
 * @author dominik.stadler at gmx.at
 */
object ListRemotes {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			Git(repository).use { git ->
				var refs = git.lsRemote().call()
				for (ref in refs) {
					println("Ref: $ref")
				}

				// heads only
				refs = git.lsRemote().setHeads(true).call()
				for (ref in refs) {
					println("Head: $ref")
				}

				// tags only
				refs = git.lsRemote().setTags(true).call()
				for (ref in refs) {
					println("Remote tag: $ref")
				}
			}
		}
	}
}