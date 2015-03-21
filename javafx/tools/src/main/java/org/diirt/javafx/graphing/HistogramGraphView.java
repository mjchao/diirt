/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.diirt.javafx.graphing;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import static org.diirt.datasource.formula.ExpressionLanguage.formula;
import static org.diirt.datasource.graphene.ExpressionLanguage.histogramGraphOf;
import org.diirt.datasource.graphene.Graph2DExpression;
import org.diirt.datasource.graphene.HistogramGraph2DExpression;
import org.diirt.graphene.AreaGraph2DRendererUpdate;

/**
 *
 * @author Mickey
 */
public class HistogramGraphView extends BaseGraphView< AreaGraph2DRendererUpdate > {

    private BooleanProperty highlightFocusValue = new SimpleBooleanProperty( this , "Highlight Focus" , false );
    
    private ConfigurationDialog defaultConfigurationDialog = new ConfigurationDialog();
    
    @Override
    public Graph2DExpression createExpression(String dataFormula) {
	HistogramGraph2DExpression plot = histogramGraphOf(formula(dataFormula));
	plot.update(plot.newUpdate().highlightFocusValue(highlightFocusValue.getValue()));
	return plot;
    }
    
    public HistogramGraphView() {
	this.highlightFocusValue.addListener( new ChangeListener< Boolean >() {

	    @Override
	    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		graph.update( graph.newUpdate().highlightFocusValue( newValue ) );
	    }
	});
	
	this.defaultConfigurationDialog.addBooleanProperty( this.highlightFocusValue );
    }
    
    @Override
    protected void onMouseMove(MouseEvent e) {
	if ( graph != null ) {
	    graph.update(graph.newUpdate().focusPixel( (int)e.getX() ));
	}
    }
    
    public void setHighlightFocusValue( boolean b ) {
	this.highlightFocusValue.setValue( b );
    }
    
    public boolean isHighlightFocusValue() {
	return this.highlightFocusValue.getValue();
    }
    
    public BooleanProperty highlightFocusValueProperty() {
	return this.highlightFocusValue;
    }
    
    public ConfigurationDialog getDefaultConfigurationDialog() {
	return this.defaultConfigurationDialog;
    }
}