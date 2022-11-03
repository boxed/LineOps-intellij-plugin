package net.kodare.lineops;

import com.intellij.ide.bookmarks.BookmarkManager;
import com.intellij.openapi.editor.Editor;

import java.util.ArrayList;


public class BatchBookmark extends SearchAction {
  void doAction(BookmarkManager bookmarkManager, Editor editor, ArrayList<Integer> lineNumbers) {
    for (Integer lineNumber : lineNumbers) {
      bookmarkManager.addEditorBookmark(editor, lineNumber);
    }
  }
}
