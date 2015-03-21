/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.diirt.javafx.graphing;

import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.diirt.graphene.InterpolationScheme;
import org.diirt.graphene.NumberColorMap;

/**
 * Allows the user to configure the properties of a graph (e.g. the x column of
 * a bubble graph, or the interpolation scheme of a line graph).
 * <p>
 * To create a custom <code>ConfigurationDialog</code>, in the containing class, 
 * list out what properties the user may configure. In most cases, the containing
 * class will be of type <code>BaseGraphApp</code>, and the supported property types are
 * <ul>
 *  <li> String
 *  <li> Boolean
 *  <li> InterpolationScheme
 *  <li> NumberColorMap
 * </ul>
 * 
 * <p>
 * Then find the appropriate method and add that property to the configuration
 * dialog. For some properties such as the interpolation scheme and number
 * color mapping, the user cannot choose anything s/he wants. Therefore, a list
 * of allowed properties is also required
 * <p>
 * For example, to allow a <code>NumberColorMap</code> property to be configured, 
 * simply use the code
 * <pre>
 * <code>
 * Property&lt NumberColorMap &gt p = ...
 * NumberColorMap[] allowedNumberColorMaps = ...
 * configurationDialog.addNumberColorMapListProperty( p );
 * </code>
 * </pre>
 * @author mjchao
 */
public class ConfigurationDialog extends Stage {
    
    /**
     * Contains the configuration field in which the user's current configurations
     * for a property are stored, and remembers the last saved configuration for 
     * this property
     * 
     * @param <T> the type of data used for this configuration (e.g. string, boolean)
     */
    private class ConfigurationData< T > {

	final public ConfigurationField< T > field;
	final public Property< T > lastSavedProperty;
	
	public ConfigurationData( ConfigurationField< T > f , Property< T > p ) {
	    this.field = f;
	    this.lastSavedProperty = p;
	}
    }
    
    /**
     * stores the data for all the properties in this configuration dialog
     */
    final private ArrayList< ConfigurationData > configurationData = new ArrayList< ConfigurationData >();
    
    /**
     * panel containing everything in this dialog
     */
    final private BorderPane pnlMain = new BorderPane();
    
    /**
     * panel containing the configuration fields in which the user enters
     * his/her choices
     */
    final private GridPane pnlConfigurations = new GridPane();
    
    /**
     * panel providing options for the user to save the current configurations
     * or cancel and revert to the previously saved configuration
     */
    final private GridPane pnlSaveCancel = new GridPane();
    
    /**
     * Creates a default configuration dialog with no configurations available
     */
    public ConfigurationDialog() {
	this.initStyle( StageStyle.UTILITY );
	Scene s = new Scene( pnlMain );
	pnlMain.setCenter( pnlConfigurations );
	pnlMain.setBottom( pnlSaveCancel );
	
	Button cmdConfigure = new Button( "Configure" );
	cmdConfigure.setOnAction( new EventHandler< ActionEvent >() {

	    @Override
	    public void handle(ActionEvent event) {
		saveChanges();
	    }
	});
	
	Button cmdCancel = new Button( "Cancel" );
	cmdCancel.setOnAction( new EventHandler< ActionEvent >() {

	    @Override
	    public void handle(ActionEvent event) {
		cancelChanges();
	    }
	});
	
	this.setOnHidden( new EventHandler< WindowEvent >() {

	    @Override
	    public void handle(WindowEvent event) {
		cancelChanges();
	    }
	    
	});
	
	pnlSaveCancel.add( cmdConfigure , 0 , 0 );
	pnlSaveCancel.add( cmdCancel , 1 , 0 );
	this.setScene( s );
    }
    
    /**
     * Adds the given string property as something the user may configure. In the dialog,
     * this property will have a label containing the name of the property and a
     * text field in which the user can enter his/her configuration for this
     * property.
     * 
     * @param p the string property that the user may modify
     */
    public void addStringProperty( StringProperty p ) {
	StringField newField = new StringField( p );
	this.pnlConfigurations.add( newField , 0 , this.configurationData.size() );
	ConfigurationData data = new ConfigurationData( newField , new SimpleStringProperty( p.getValue() ) );
	this.configurationData.add( data );
    }
    
    /**
     * Adds the given boolean property as something the user may configure. In the dialog,
     * this property will have a label containing the name of the property and a 
     * check box which the user can use to toggle this property to true or false.
     * 
     * @param p the boolean property that the user may modify
     */
    public void addBooleanProperty( BooleanProperty p ) {
	BooleanField newField = new BooleanField( p );
	this.pnlConfigurations.add( newField , 0 , this.configurationData.size() );
	ConfigurationData data = new ConfigurationData( newField , new SimpleBooleanProperty( p.getValue() ) );
	this.configurationData.add( data );
    }
    
    /**
     * Adds the given interpolation scheme property as something the user may
     * configure. In the dialog, this property will have a label containing the
     * name of the property and a combobox which the user can use to modify
     * this property.
     * 
     * @param p the interpolation scheme property that the user may modify
     * @param allowedInterpolations the list of allowed interpolation schemes
     */
    public void addInterpolationSchemeListProperty( Property< InterpolationScheme > p , InterpolationScheme[] allowedInterpolations ) {
	InterpolationSchemeField newField = new InterpolationSchemeField( p , allowedInterpolations );
	this.pnlConfigurations.add( newField , 0 , this.configurationData.size() );
	ConfigurationData data = new ConfigurationData( newField , new SimpleObjectProperty< InterpolationScheme >( p.getValue() ) );
	this.configurationData.add( data );
    }
    
    /**
     * Adds the given number-color mapping property as something the user may 
     * configure. In the dialog, this property will have a label containing the
     * name of the property and a combobox which the user can use to modify
     * this property.
     * 
     * @param p the number-color mapping property that the user may modify
     * @param allowedMappings the list of allowed number-color mappings
     */
    public void addNumberColorMapListProperty( Property< NumberColorMap > p , NumberColorMap[] allowedMappings ) {
	NumberColorMapField newField = new NumberColorMapField( p , allowedMappings );
	this.pnlConfigurations.add( newField , 0 , this.configurationData.size() );
	ConfigurationData data = new ConfigurationData( newField , new SimpleObjectProperty< NumberColorMap >( p.getValue() ) );
	this.configurationData.add( data );
    }
    
    /**
     * Saves the current configuration state so that the user may revert to it
     * later if necessary
     */
    public void saveChanges() {
	for ( ConfigurationData d : this.configurationData ) {
	    d.lastSavedProperty.setValue( d.field.getValue() );
	}
    }
    
    /**
     * Discards the user's current configurations and reverts to the previous
     * saved configuration state
     */
    public void cancelChanges() {
	loadSaved();
    }
    
    /**
     * Loads the previously saved configuration state, discarding any 
     * configurations that may currently exist.
     */
    public void loadSaved() {
	for ( ConfigurationData d : this.configurationData ) {
	    d.field.setValue( d.lastSavedProperty.getValue() );
	}
    }
    
    /**
     * Opens this configuration dialog.
     */
    public void open() {
	this.show();
	this.toFront();
	this.sizeToScene();
	this.loadSaved();
    }
    
    /**
     * Provides the user with the ability to configure one property
     * 
     * @param <T> the type of property this field allows the user to modify
     */
    private class ConfigurationField< T > extends FlowPane {
	
	/**
	 * the property the user is allowed to modify
	 */
	final private Property< T > m_property;
	
	/**
	 * Creates a configuration field for the given property.
	 * 
	 * @param property the property the user may configure
	 * @param onPropertyChanged what to do if the user changes the property
	 */
	public ConfigurationField( Property< T > property ) {
	    this.m_property = property;
	}
	
	public Property< T > property() {
	    return this.m_property;
	}
	
	public void setValue( T value ) {
	    this.m_property.setValue( value );
	}
	
	public T getValue() {
	    return this.m_property.getValue();
	}
    }
    
    /**
     * Creates a default name label for a property. This simply takes the given
     * property name, and appends ":     " to it.
     * 
     * @param propertyName the name of a property
     * @return a label with the default name display
     */
    private static Label defaultNameLabel( String propertyName ) {
	return new Label( propertyName + ":     " );
    }
    
    /**
     * Provides the user with the ability to configure a string property
     */
    private class StringField extends ConfigurationField< String > {
	
	/**
	 * shows the name of the property
	 */
	final private Label lblName;
	
	/**
	 * shows the value of the property, and allows the user to change
	 * the value of the property by typing something in this text field
	 */
	final private TextField txtValue;
	
	/**
	 * Creates a default string field that allows the user to configure
	 * a text-based property
	 * 
	 * @param p the property the user can configure
	 */
	public StringField( StringProperty p ) {
	    super( p );
	    this.lblName = defaultNameLabel( p.getName() );
	    this.txtValue = new TextField( "         " );
	    this.txtValue.textProperty().bindBidirectional( p );
	    this.txtValue.setText( p.getValue() );
	    this.getChildren().addAll( this.lblName , this.txtValue );
	}
    }
    
    /**
     * Provides the user with the ability to configure a boolean property
     */
    private class BooleanField extends ConfigurationField< Boolean > {
	
	/**
	 * shows the name of the property
	 */
	final private Label lblName;
	
	/**
	 * allows the user to configure the property by toggling it with this
	 * check box
	 */
	final private CheckBox chkValue;
	
	/**
	 * Creates a default boolean field that allows the user to configure
	 * a boolean property 
	 * 
	 * @param p the property the user can configure
	 */
	public BooleanField( BooleanProperty p ) {
	    super( p );
	    this.lblName = defaultNameLabel( p.getName() );
	    this.chkValue = new CheckBox();
	    this.chkValue.selectedProperty().bindBidirectional( p );
	    this.chkValue.setSelected( p.getValue() );
	    this.getChildren().addAll( this.lblName , this.chkValue );
	}
    }
    
    /**
     * Provides the user with the ability to select an InterpolationScheme
     */
    private class InterpolationSchemeField extends ConfigurationField< InterpolationScheme > {

	final private Label lblName;
	final private ComboBox< InterpolationScheme > cboInterpolations;
	
	/**
	 * Creates an InterpolationSchemeField that allows the user to configure
	 * an interpolation scheme property by selecting an interpolation scheme
	 * from a list of allowed interpolation schemes.
	 * 
	 * @param p the interpolation scheme property the user can configure
	 * @param interpolationSchemes the list of allowed interpolation schemes,
	 * which must be nonempty
	 * @throws IllegalArgumentException if the list of allowed interpolation
	 * schemes is empty
	 */
	public InterpolationSchemeField( Property<InterpolationScheme> p , InterpolationScheme[] interpolationSchemes ) {
	    super( p );
	    if ( interpolationSchemes.length == 0 ) {
		throw new IllegalArgumentException( "Must have at least 1 allowed interpolation scheme." );
	    }
	    this.lblName = defaultNameLabel( p.getName() );
	    this.cboInterpolations = new ComboBox< InterpolationScheme >();
	    this.cboInterpolations.getItems().addAll( interpolationSchemes );
	    this.cboInterpolations.valueProperty().bindBidirectional( p );
	    this.cboInterpolations.setValue( p.getValue() );
	    this.getChildren().addAll( this.lblName , this.cboInterpolations );
	}
    }
    
    /**
     * Provides the user with the ability to select a NumberColorMap property
     */
    private class NumberColorMapField extends ConfigurationField< NumberColorMap > {
	
	final private Label lblName;
	final private ComboBox< NumberColorMap > cboColorMaps = new ComboBox< NumberColorMap >();
	
	/**
	 * Creates a NumberColorMapField that allows the user to configure a
	 * number-color mapping by selecting one from a list of allowed 
	 * mappings
	 * 
	 * @param p the property the user can configure
	 * @param maps the allowed mappings the user can select
	 * @throws IllegalArugmentException if there are no allowed mapping from which the user can select
	 */
	public NumberColorMapField( Property< NumberColorMap > p , NumberColorMap[] maps ) {
	    super( p );
	    if ( maps.length == 0 ) {
		throw new IllegalArgumentException( "Must have at least 1 allowed number-color mapping" );
	    }
	    this.lblName = defaultNameLabel( p.getName() );
	    this.cboColorMaps.getItems().addAll( maps );
	    this.cboColorMaps.valueProperty().bindBidirectional( p );
	    this.cboColorMaps.setValue( p.getValue() );
	    this.getChildren().addAll( this.lblName , this.cboColorMaps );
	}
    }
}