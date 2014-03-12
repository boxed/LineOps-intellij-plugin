package net.kodare.lineops;

import com.intellij.ide.bookmarks.BookmarkManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.util.ArrayList;

public abstract class SearchAction extends AnAction {

  abstract void doAction(BookmarkManager bookmarkManager, Editor editor, ArrayList<Integer> lineNumbers);

  public void actionPerformed(AnActionEvent e) {
    Project project = e.getData(CommonDataKeys.PROJECT);
    Editor editor = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE);
    if (editor == null) {
      return;
    }

    Document document = editor.getDocument();
    String searchString = Messages.showInputDialog(project, "Bulk Bookmark", "Search:", Messages.getQuestionIcon());
    if (searchString == null) {
      return;
    }

    BookmarkManager bookmarkManager = BookmarkManager.getInstance(project);

    int index = document.getText().indexOf(searchString);
    ArrayList<Integer> lineNumbers = new ArrayList<Integer>();
    while (index != -1) {
      lineNumbers.add(document.getLineNumber(index));
      index = document.getText().indexOf(searchString, index+1);
    }

    doAction(bookmarkManager, editor, lineNumbers);
  }}
