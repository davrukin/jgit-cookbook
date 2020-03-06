package org.dstadler.jgit

import org.apache.commons.io.FileUtils
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
 * Simple snippet which shows how to create a new repository
 *
 * @author dominik.stadler at gmx.at
 */
object CreateNewRepository {

	@Throws(IOException::class, IllegalStateException::class, GitAPIException::class)
	@JvmStatic
	@JvmOverloads
	fun main(args: Array<String> = arrayOf()) {
		// prepare a new folder
		val localPath = File.createTempFile("TestGitRepository", "")
		if (!localPath.delete()) {
			throw IOException("Could not delete temporary file $localPath")
		}
		Git.init().setDirectory(localPath).call().use { git -> println("Having repository: " + git.repository.directory) }

		// clean up here to not keep using more and more disk-space for these samples
		FileUtils.deleteDirectory(localPath)
	}
}