package net.kodare.lineops;

import com.intellij.ide.bookmark.BookmarksManager;
import com.intellij.ide.bookmark.providers.LineBookmarkProvider;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;

public abstract class SearchAction extends AnAction {

  abstract void doAction(BookmarksManager bookmarksManager, LineBookmarkProvider lineBookmarkProvider, VirtualFile virtualFile, ArrayList<Integer> lineNumbers);

  @SuppressWarnings("UnstableApiUsage")
  abstract @NlsContexts.DialogTitle String getTitle();

  public void actionPerformed(AnActionEvent e) {
    final Project project = e.getData(CommonDataKeys.PROJECT);
    if (project == null) {
      return;
    }
    final Editor editor = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE);
    final var bookmarkManager = BookmarksManager.getInstance(project);
    final var lineBookmarkProvider = LineBookmarkProvider.find(project);
    final VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
    if (editor == null || lineBookmarkProvider == null || file == null) {
      return;
    }

    final var document = editor.getDocument();
    final String searchString = Messages.showInputDialog(project, "Search:", getTitle(), Messages.getQuestionIcon());
    if (searchString == null) {
      return;
    }

    int index = document.getText().indexOf(searchString);
    ArrayList<Integer> lineNumbers = new ArrayList<>();
    while (index != -1) {
      lineNumbers.add(document.getLineNumber(index));
      index = document.getText().indexOf(searchString, index+1);
    }

    doAction(bookmarkManager, lineBookmarkProvider, file, lineNumbers);
  }}
