package net.kodare.lineops;

import com.intellij.ide.bookmark.BookmarksManager;
import com.intellij.ide.bookmark.LineBookmark;
import com.intellij.ide.bookmark.providers.LineBookmarkProvider;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class BatchRemoveBookmark extends SearchAction {
  @Override
  void doAction(BookmarksManager bookmarksManager, LineBookmarkProvider lineBookmarkProvider, VirtualFile virtualFile, ArrayList<Integer> lineNumbers) {
    final Set<Integer> set = new HashSet<>(lineNumbers);
    for (com.intellij.ide.bookmark.Bookmark b: bookmarksManager.getBookmarks()) {
      if (b instanceof LineBookmark lineBookmark
              && lineBookmark.getFile().equals(virtualFile)
              && set.contains(lineBookmark.getLine())) {
        bookmarksManager.remove(b);
      }
    }
  }

  @Override
  String getTitle() {
    return "Batch Unbookmark";
  }
}