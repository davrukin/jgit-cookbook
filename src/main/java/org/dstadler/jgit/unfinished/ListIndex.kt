package org.dstadler.jgit.unfinished

import org.dstadler.jgit.helper.CookbookHelper.openJGitCookbookRepository
import org.eclipse.jgit.dircache.DirCache
import org.eclipse.jgit.dircache.DirCacheEntry
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
 * Snippet which shows how to work with the Index
 *
 * @author dominik.stadler at gmx.at
 */
object ListIndex {
	@Throws(IOException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		openJGitCookbookRepository().use { repository ->
			// DirCache contains all files of the repository
			val index = DirCache.read(repository)
			println("DirCache has " + index.entryCount + " items")
			for (i in 0 until index.entryCount) {
				// the number after the AnyObjectId is the "stage", see the constants in DirCacheEntry
				println("Item " + i + ": " + index.getEntry(i))
			}

			//
			println("Now printing staged items...")
			for (i in 0 until index.entryCount) {
				val entry = index.getEntry(i)
				if (entry.stage != DirCacheEntry.STAGE_0) {
					println("Item $i: $entry")
				}
			}
		}
	}
}