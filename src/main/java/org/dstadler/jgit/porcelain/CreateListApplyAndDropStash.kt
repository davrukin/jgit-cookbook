package org.dstadler.jgit.porcelain

import org.apache.commons.io.FileUtils
import org.dstadler.jgit.helper.CookbookHelper.createNewRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import java.io.File
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
 * Simple snippet which shows how to use commands for stashing changes.
 *
 * @author dominik.stadler at gmx.at
 */
object CreateListApplyAndDropStash {

	@Throws(IOException::class, GitAPIException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		val localPath = createNewRepository().use { repository ->
			val path = repository.workTree
			Git(repository).use { git ->
				// create a file
				val file1 = File(repository.directory.parent, "testfile")
				FileUtils.writeStringToFile(file1, "some text", "UTF-8")
				val file2 = File(repository.directory.parent, "testfile2")
				FileUtils.writeStringToFile(file2, "some text", "UTF-8")

				// add and commit the file
				git.add()
						.addFilepattern("testfile")
						.call()
				git.add()
						.addFilepattern("testfile2")
						.call()
				git.commit()
						.setMessage("Added testfiles")
						.call()

				// then modify the file
				FileUtils.writeStringToFile(file1, "some more text", "UTF-8", true)

				// push the changes to a new stash
				var stash = git.stashCreate()
						.call()
				println("Created stash $stash")

				// then modify the 2nd file
				FileUtils.writeStringToFile(file2, "some more text", "UTF-8", true)

				// push the changes to a new stash
				stash = git.stashCreate()
						.call()
				println("Created stash $stash")

				// list the stashes
				val stashes = git.stashList().call()
				for (rev in stashes) {
					println("Found stash: " + rev + ": " + rev.fullMessage)
				}

				// drop the 1st stash without applying it
				val call = git.stashDrop().setStashRef(0).call()
				println("StashDrop returned: $call")
				val applied = git.stashApply().setStashRef(stash.name).call()
				println("Applied 2nd stash as: $applied")

				path
			}
		}

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(localPath)
	}
}