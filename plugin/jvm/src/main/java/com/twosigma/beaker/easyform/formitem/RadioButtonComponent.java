package com.twosigma.beaker.easyform.formitem;

import com.twosigma.beaker.easyform.EasyFormComponent;

import java.util.Collection;

public class RadioButtonComponent extends EasyFormComponent {

    private Boolean horizontal;
    private Collection<String> values;

    public void setHorizontal(final Boolean horizontal) {
        this.horizontal = horizontal;
    }

    public Boolean getHorizontal() {
        return horizontal;
    }

    public void setValues(final Collection<String> values) {
        this.values = values;
    }

    public Collection<String> getValues() {
        return values;
    }
}