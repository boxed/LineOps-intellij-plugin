package net.kodare.lineops;

import com.intellij.ide.bookmark.BookmarkType;
import com.intellij.ide.bookmark.BookmarksManager;
import com.intellij.ide.bookmark.FileBookmark;
import com.intellij.ide.bookmark.providers.LineBookmarkProvider;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;


public class BatchBookmark extends SearchAction {

  @Override
  void doAction(BookmarksManager bookmarksManager, LineBookmarkProvider lineBookmarkProvider, VirtualFile virtualFile, ArrayList<Integer> lineNumbers) {
    for (Integer lineNumber : lineNumbers) {
      final FileBookmark bookmark = lineBookmarkProvider.createBookmark(virtualFile, lineNumber);
      if (bookmark == null) {
        continue;
      }
      bookmarksManager.add(bookmark, BookmarkType.DEFAULT);
    }
  }

  @Override
  String getTitle() {
    return "Batch Bookmark";
  }
}
