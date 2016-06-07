//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5.1 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.06.07 at 07:36:42 PM CEST 
//


package model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Stavovi" type="{http://www.ftn.uns.ac.rs/skupstina}StavType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Brojcana_oznaka" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="Naziv" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "stavovi"
})
@XmlRootElement(name = "Clan")
public class Clan {

    @XmlElement(name = "Stavovi", required = true)
    protected List<StavType> stavovi;
    @XmlAttribute(name = "Brojcana_oznaka", required = true)
    protected int brojcanaOznaka;
    @XmlAttribute(name = "Naziv", required = true)
    protected String naziv;

    /**
     * Gets the value of the stavovi property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stavovi property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStavovi().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StavType }
     * 
     * 
     */
    public List<StavType> getStavovi() {
        if (stavovi == null) {
            stavovi = new ArrayList<StavType>();
        }
        return this.stavovi;
    }

    /**
     * Gets the value of the brojcanaOznaka property.
     * 
     */
    public int getBrojcanaOznaka() {
        return brojcanaOznaka;
    }

    /**
     * Sets the value of the brojcanaOznaka property.
     * 
     */
    public void setBrojcanaOznaka(int value) {
        this.brojcanaOznaka = value;
    }

    /**
     * Gets the value of the naziv property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNaziv() {
        return naziv;
    }

    /**
     * Sets the value of the naziv property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNaziv(String value) {
        this.naziv = value;
    }

}
