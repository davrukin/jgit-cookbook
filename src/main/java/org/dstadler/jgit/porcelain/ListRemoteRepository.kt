package org.dstadler.jgit.porcelain

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException

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
 * Simple snippet which shows how to list heads/tags of remote repositories without
 * a local repository
 *
 * @author dominik.stadler at gmx.at
 */
object ListRemoteRepository {
	private const val REMOTE_URL = "https://github.com/github/testrepo.git"

	@Throws(GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		// then clone
		println("Listing remote repository $REMOTE_URL")
		var refs = Git.lsRemoteRepository()
				.setHeads(true)
				.setTags(true)
				.setRemote(REMOTE_URL)
				.call()
		for (ref in refs) {
			println("Ref: $ref")
		}
		val map = Git.lsRemoteRepository()
				.setHeads(true)
				.setTags(true)
				.setRemote(REMOTE_URL)
				.callAsMap()
		println("As map")
		for ((key, value) in map) {
			println("Key: $key, Ref: $value")
		}
		refs = Git.lsRemoteRepository()
				.setRemote(REMOTE_URL)
				.call()
		println("All refs")
		for (ref in refs) {
			println("Ref: $ref")
		}
	}
}