package com.example.demo2;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class OpenInBrowserAction extends AnAction {
    public Map<String, String> map = new HashMap<>();
    public OpenInBrowserAction(){
        map.put("component", "Ext.Component");
        map.put("box", "Ext.Component");
        map.put("editor", "Ext.Editor");
        map.put("image", "Ext.Img");
        map.put("imagecomponent", "Ext.Img");
        map.put("loadmask", "Ext.LoadMask");
        map.put("progress", "Ext.Progress");
        map.put("progressbarwidget", "Ext.Progress");
        map.put("progressbar", "Ext.ProgressBar");
        map.put("widget", "Ext.Widget");
        map.put("button", "Ext.button.Button");
        map.put("cycle", "Ext.button.Cycle");
        map.put("segmentedbutton", "Ext.button.Segmented");
        map.put("splitbutton", "Ext.button.Split");
        map.put("calendar", "Ext.calendar.panel.Panel");
        map.put("cartesian", "Ext.chart.CartesianChart");
        map.put("chart", "Ext.chart.CartesianChart");
        map.put("polar", "Ext.chart.PolarChart");
        map.put("spacefilling", "Ext.chart.SpaceFillingChart");
        map.put("axis", "Ext.chart.axis.Axis");
        map.put("axis3d", "Ext.chart.axis.Axis3D");
        map.put("interaction", "Ext.chart.interactions.Abstract");
        map.put("chartnavigator", "Ext.chart.navigator.Container");
        map.put("buttongroup", "Ext.container.ButtonGroup");
        map.put("container", "Ext.container.Container");
        map.put("viewport", "Ext.container.Viewport");
        map.put("dashboard", "Ext.dashboard.Dashboard");
        map.put("draw", "Ext.draw.Container");
        map.put("surface", "Ext.draw.Surface");
        map.put("flash", "Ext.flash.Component");
        map.put("checkboxgroup", "Ext.form.CheckboxGroup");
        map.put("fieldcontainer", "Ext.form.FieldContainer");
        map.put("fieldset", "Ext.form.FieldSet");
        map.put("label", "Ext.form.Label");
        map.put("form", "Ext.form.Panel");
        map.put("radiogroup", "Ext.form.RadioGroup");
        map.put("field", "Ext.form.field.Base");
        map.put("checkboxfield", "Ext.form.field.Checkbox");
        map.put("checkbox", "Ext.form.field.Checkbox");
        map.put("combobox", "Ext.form.field.ComboBox");
        map.put("combo", "Ext.form.field.ComboBox");
        map.put("datefield", "Ext.form.field.Date");
        map.put("displayfield", "Ext.form.field.Display");
        map.put("filefield", "Ext.form.field.File");
        map.put("fileuploadfield", "Ext.form.field.File");
        map.put("filebutton", "Ext.form.field.FileButton");
        map.put("hiddenfield", "Ext.form.field.Hidden");
        map.put("hidden", "Ext.form.field.Hidden");
        map.put("htmleditor", "Ext.form.field.HtmlEditor");
        map.put("numberfield", "Ext.form.field.Number");
        map.put("pickerfield", "Ext.form.field.Picker");
        map.put("radiofield", "Ext.form.field.Radio");
        map.put("radio", "Ext.form.field.Radio");
        map.put("spinnerfield", "Ext.form.field.Spinner");
        map.put("tagfield", "Ext.form.field.Tag");
        map.put("textfield", "Ext.form.field.Text");
        map.put("textareafield", "Ext.form.field.TextArea");
        map.put("textarea", "Ext.form.field.TextArea");
        map.put("timefield", "Ext.form.field.Time");
        map.put("triggerfield", "Ext.form.field.Trigger");
        map.put("trigger", "Ext.form.field.Trigger");
        map.put("froalaeditor", "Ext.froala.Editor");
        map.put("froalaeditorfield", "Ext.froala.EditorField");
        map.put("celleditor", "Ext.grid.CellEditor");
        map.put("gridpanel", "Ext.grid.Panel");
        map.put("grid", "Ext.grid.Panel");
        map.put("roweditor", "Ext.grid.RowEditor");
        map.put("roweditorbuttons", "Ext.grid.RowEditorButtons");
        map.put("actioncolumn", "Ext.grid.column.Action");
        map.put("booleancolumn", "Ext.grid.column.Boolean");
        map.put("checkcolumn", "Ext.grid.column.Check");
        map.put("gridcolumn", "Ext.grid.column.Column");
        map.put("datecolumn", "Ext.grid.column.Date");
        map.put("groupscolumn", "Ext.grid.column.Groups");
        map.put("numbercolumn", "Ext.grid.column.Number");
        map.put("rownumberer", "Ext.grid.column.RowNumberer");
        map.put("templatecolumn", "Ext.grid.column.Template");
        map.put("widgetcolumn", "Ext.grid.column.Widget");
        map.put("headercontainer", "Ext.grid.header.Container");
        map.put("groupingpanelcolumn", "Ext.grid.plugin.grouping.Column");
        map.put("groupingpanel", "Ext.grid.plugin.grouping.Panel");
        map.put("propertygrid", "Ext.grid.property.Grid");
        map.put("columnsplitter", "Ext.layout.container.ColumnSplitter");
        map.put("treelist", "Ext.list.Tree");
        map.put("treelistitem", "Ext.list.TreeItem");
        map.put("menubar", "Ext.menu.Bar");
        map.put("menucheckitem", "Ext.menu.CheckItem");
        map.put("colormenu", "Ext.menu.ColorPicker");
        map.put("datemenu", "Ext.menu.DatePicker");
        map.put("menuitem", "Ext.menu.Item");
        map.put("menu", "Ext.menu.Menu");
        map.put("menuseparator", "Ext.menu.Separator");
        map.put("header", "Ext.panel.Header");
        map.put("panel", "Ext.panel.Panel");
        map.put("tablepanel", "Ext.panel.Table");
        map.put("title", "Ext.panel.Title");
        map.put("tool", "Ext.panel.Tool");
        map.put("colorpicker", "Ext.picker.Color");
        map.put("datepicker", "Ext.picker.Date");
        map.put("monthpicker", "Ext.picker.Month");
        map.put("timepicker", "Ext.picker.Time");
        map.put("pivotgrid", "Ext.pivot.Grid");
        map.put("mzpivotgrid", "Ext.pivot.Grid");
        map.put("pivotd3container", "Ext.pivot.d3.Container");
        map.put("pivotd3container", "Ext.pivot.d3.Container");
        map.put("pivotheatmap", "Ext.pivot.d3.HeatMap");
        map.put("pivottreemap", "Ext.pivot.d3.TreeMap");
        map.put("pivotconfigfield", "Ext.pivot.plugin.configurator.Column");
        map.put("pivotconfigcontainer", "Ext.pivot.plugin.configurator.Container");
        map.put("pivotconfigpanel", "Ext.pivot.plugin.configurator.Panel");
        map.put("bordersplitter", "Ext.resizer.BorderSplitter");
        map.put("splitter", "Ext.resizer.Splitter");
        map.put("multislider", "Ext.slider.Multi");
        map.put("slider", "Ext.slider.Single");
        map.put("sliderfield", "Ext.slider.Single");
        map.put("slidertip", "Ext.slider.Tip");
        map.put("sliderwidget", "Ext.slider.Widget");
        map.put("sparklinebar", "Ext.sparkline.Bar");
        map.put("sparkline", "Ext.sparkline.Base");
        map.put("sparklinebox", "Ext.sparkline.Box");
        map.put("sparklinebullet", "Ext.sparkline.Bullet");
        map.put("sparklinediscrete", "Ext.sparkline.Discrete");
        map.put("sparklineline", "Ext.sparkline.Line");
        map.put("sparklinepie", "Ext.sparkline.Pie");
        map.put("sparklinetristate", "Ext.sparkline.TriState");
        map.put("tabbar", "Ext.tab.Bar");
        map.put("tabpanel", "Ext.tab.Panel");
        map.put("tab", "Ext.tab.Tab");
        map.put("quicktip", "Ext.tip.QuickTip");
        map.put("tip", "Ext.tip.Tip");
        map.put("tooltip", "Ext.tip.ToolTip");
        map.put("breadcrumb", "Ext.toolbar.Breadcrumb");
        map.put("tbfill", "Ext.toolbar.Fill");
        map.put("tbitem", "Ext.toolbar.Item");
        map.put("pagingtoolbar", "Ext.toolbar.Paging");
        map.put("tbseparator", "Ext.toolbar.Separator");
        map.put("tbspacer", "Ext.toolbar.Spacer");
        map.put("tbtext", "Ext.toolbar.TextItem");
        map.put("toolbar", "Ext.toolbar.Toolbar");
        map.put("treecolumn", "Ext.tree.Column");
        map.put("treepanel", "Ext.tree.Panel");
        map.put("treeview", "Ext.tree.View");
        map.put("explorer", "Ext.ux.Explorer");
        map.put("gmappanel", "Ext.ux.GMapPanel");
        map.put("uxiframe", "Ext.ux.IFrame");
        map.put("treepicker", "Ext.ux.TreePicker");
        map.put("colorbutton", "Ext.ux.colorpick.Button");
        map.put("colorpickercolormap", "Ext.ux.colorpick.ColorMap");
        map.put("colorpickercolorpreview", "Ext.ux.colorpick.ColorPreview");
        map.put("colorfield", "Ext.ux.colorpick.Field");
        map.put("colorselector", "Ext.ux.colorpick.Selector");
        map.put("colorpickerslider", "Ext.ux.colorpick.Slider");
        map.put("colorpickerslideralpha", "Ext.ux.colorpick.SliderAlpha");
        map.put("colorpickersliderhue", "Ext.ux.colorpick.SliderHue");
        map.put("colorpickerslidersaturation", "Ext.ux.colorpick.SliderSaturation");
        map.put("colorpickerslidervalue", "Ext.ux.colorpick.SliderValue");
        map.put("desktop", "Ext.ux.desktop.Desktop");
        map.put("taskbar", "Ext.ux.desktop.TaskBar");
        map.put("trayclock", "Ext.ux.desktop.TrayClock");
        map.put("video", "Ext.ux.desktop.Video");
        map.put("wallpaper", "Ext.ux.desktop.Wallpaper");
        map.put("eventrecordermanager", "Ext.ux.event.RecorderManager");
        map.put("itemselectorfield", "Ext.ux.form.ItemSelector");
        map.put("itemselector", "Ext.ux.form.ItemSelector");
        map.put("multiselectfield", "Ext.ux.form.MultiSelect");
        map.put("multiselect", "Ext.ux.form.MultiSelect");
        map.put("searchfield", "Ext.ux.form.SearchField");
        map.put("gauge", "Ext.ux.gauge.Gauge");
        map.put("rating", "Ext.ux.rating.Picker");
        map.put("statusbar", "Ext.ux.statusbar.StatusBar");
        map.put("boundlist", "Ext.view.BoundList");
        map.put("multiselector", "Ext.view.MultiSelector");
        map.put("tableview", "Ext.view.Table");
        map.put("gridview", "Ext.view.Table");
        map.put("dataview", "Ext.view.View");
        map.put("messagebox", "Ext.window.MessageBox");
        map.put("toast", "Ext.window.Toast");
        map.put("window", "Ext.window.Window");


    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            Messages.showErrorDialog(project, "Editor not found", "Error");
            return;
        }

        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();

        if (selectedText != null && !selectedText.isEmpty()) {
            // 检查 selectedText 是否在 map 中
            if (map.containsKey(selectedText)) {
                selectedText = map.get(selectedText);
            }

            // 检查 selectedText 是否以 "Ext." 开头
            if (selectedText.startsWith("Ext.")) {
                try {
                    String url = "https://extjs-xs.dmds.xyz/extjs/7.8.0/classic/" + selectedText+".html";
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception e) {
                    Messages.showErrorDialog(project, "Failed to open browser: " + e.getMessage(), "Error");
                }
            } else {
                Messages.showInfoMessage(project, "Selected text does not start with 'Ext.'", "Info");
            }
        } else {
            Messages.showInfoMessage(project, "No text selected", "Info");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        PsiFile file = event.getData(CommonDataKeys.PSI_FILE);
        event.getPresentation().setEnabledAndVisible(editor != null && isJavaScriptFile(file));
    }

    private boolean isJavaScriptFile(PsiFile file) {
        return file != null && "JavaScript".equalsIgnoreCase(file.getFileType().getName());
    }

    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT; // 选择合适的线程，EDT 或 BGT
    }
}