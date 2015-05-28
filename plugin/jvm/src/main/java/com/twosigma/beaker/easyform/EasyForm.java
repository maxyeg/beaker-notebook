package com.twosigma.beaker.easyform;

import com.twosigma.beaker.easyform.formitem.ButtonComponent;
import com.twosigma.beaker.easyform.formitem.CheckBox;
import com.twosigma.beaker.easyform.formitem.ComboBox;
import com.twosigma.beaker.easyform.formitem.DatePickerComponent;
import com.twosigma.beaker.easyform.formitem.ListComponent;
import com.twosigma.beaker.easyform.formitem.RadioButtonComponent;
import com.twosigma.beaker.easyform.formitem.TextArea;
import com.twosigma.beaker.easyform.formitem.TextField;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EasyForm {

    private Map<String, EasyFormComponent> componentMap = new HashMap<>();

    public void setEnabled(final String label, final Boolean enabled) {
        if (StringUtils.isNotEmpty(label) && componentMap.containsKey(label)) {
            componentMap.get(label).setEnabled(enabled);
        }
    }

    public void addTextField(final String label, final Integer width) throws Exception {
        TextField textField = new TextField();
        textField.setLabel(label);
        textField.setWidth(width);
        addComponentOrThrow(label, textField);
    }

    public void addTextArea(final String label) throws Exception {
        TextArea textArea = new TextArea();
        textArea.setLabel(label);
        addComponentOrThrow(label, textArea);
    }

    public void addCheckBox(final String label, final Boolean value) throws Exception {
        CheckBox checkBox = new CheckBox();
        checkBox.setLabel(label);
        checkBox.setValue(value);
        addComponentOrThrow(label, checkBox);
    }

    public void addComboBox(final String label, final Boolean editable, final Collection<String> values) throws Exception {
        ComboBox comboBox = new ComboBox();
        comboBox.setLabel(label);
        comboBox.setEditable(editable);
        comboBox.setValues(values);
        addComponentOrThrow(label, comboBox);
    }

    public void addList(final String label, final Integer size, final Boolean multipleSelection,
                        final Collection<String> values) throws Exception {
        ListComponent list = new ListComponent();
        list.setLabel(label);
        list.setSize(size);
        list.setMultipleSelection(multipleSelection);
        list.setValues(values);
        addComponentOrThrow(label, list);
    }

    public void addRadioButtons(final String label, final Boolean isHorizontal, final Collection<String> values) throws Exception {
        RadioButtonComponent radioButtonComponent = new RadioButtonComponent();
        radioButtonComponent.setLabel(label);
        radioButtonComponent.setHorizontal(isHorizontal);
        radioButtonComponent.setValues(values);
        addComponentOrThrow(label, radioButtonComponent);
    }

    public void addDatePicker(final String label, final Boolean showTime) throws Exception {
        DatePickerComponent datePickerComponent = new DatePickerComponent();
        datePickerComponent.setLabel(label);
        datePickerComponent.setShowTime(showTime);
        addComponentOrThrow(label, datePickerComponent);

    }

    public void addButton(final String label, final String actionCellTag) throws Exception {
        ButtonComponent buttonComponent = new ButtonComponent();
        buttonComponent.setLabel(label);
        buttonComponent.setTag(actionCellTag);
        addComponentOrThrow(label, buttonComponent);
    }

    private void addComponentOrThrow(final String label, final EasyFormComponent component) throws Exception {
        if (getComponentMap().containsKey(label)) {
            throw new Exception(String.format("EasyForm already contains component with such label: %s.", label));
        } else {
            getComponentMap().put(label, component);
        }
    }

    public Map<String, EasyFormComponent> getComponentMap() {
        return componentMap;
    }

    public boolean hasComponents() {
        return getComponentMap().size() > 0;
    }
}