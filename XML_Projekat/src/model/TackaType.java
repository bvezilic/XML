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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TackaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TackaType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ftn.uns.ac.rs/skupstina}Tacka"/>
 *         &lt;sequence minOccurs="0">
 *           &lt;element name="Podtacke" type="{http://www.ftn.uns.ac.rs/skupstina}PodtackaType" maxOccurs="unbounded" minOccurs="2"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TackaType", propOrder = {
    "tacka",
    "podtacke"
})
public class TackaType {

    @XmlElement(name = "Tacka", required = true)
    protected String tacka;
    @XmlElement(name = "Podtacke")
    protected List<PodtackaType> podtacke;

    /**
     * Gets the value of the tacka property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTacka() {
        return tacka;
    }

    /**
     * Sets the value of the tacka property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTacka(String value) {
        this.tacka = value;
    }

    /**
     * Gets the value of the podtacke property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the podtacke property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPodtacke().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PodtackaType }
     * 
     * 
     */
    public List<PodtackaType> getPodtacke() {
        if (podtacke == null) {
            podtacke = new ArrayList<PodtackaType>();
        }
        return this.podtacke;
    }

}