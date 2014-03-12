package net.kodare.lineops;

import com.intellij.ide.bookmarks.Bookmark;
import com.intellij.ide.bookmarks.BookmarkManager;
import com.intellij.openapi.editor.Editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BatchRemoveBookmark extends SearchAction {
  void doAction(BookmarkManager bookmarkManager, Editor editor, ArrayList<Integer> lineNumbers) {
    List<Bookmark> foo = bookmarkManager.getValidBookmarks();
    Set<Integer> set = new HashSet<Integer>(lineNumbers);
    for (Bookmark b : foo) {
      if (set.contains(b.getLine())) {
        bookmarkManager.removeBookmark(b);
      }
    }
  }
}