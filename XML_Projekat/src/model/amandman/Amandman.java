//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5.1 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.06.14 at 08:47:53 PM CEST 
//


package model.amandman;

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
 *         &lt;element ref="{http://www.ftn.uns.ac.rs/amandman}Kontekst"/>
 *         &lt;element ref="{http://www.ftn.uns.ac.rs/amandman}Operacija"/>
 *         &lt;element ref="{http://www.ftn.uns.ac.rs/amandman}Sadrzaj" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Naziv" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "kontekst",
    "operacija",
    "sadrzaj"
})
@XmlRootElement(name = "Amandman")
public class Amandman {

    @XmlElement(name = "Kontekst", required = true)
    protected Kontekst kontekst;
    @XmlElement(name = "Operacija", required = true)
    protected String operacija;
    @XmlElement(name = "Sadrzaj")
    protected Sadrzaj sadrzaj;
    @XmlAttribute(name = "Naziv")
    protected String naziv;

    /**
     * Gets the value of the kontekst property.
     * 
     * @return
     *     possible object is
     *     {@link Kontekst }
     *     
     */
    public Kontekst getKontekst() {
        return kontekst;
    }

    /**
     * Sets the value of the kontekst property.
     * 
     * @param value
     *     allowed object is
     *     {@link Kontekst }
     *     
     */
    public void setKontekst(Kontekst value) {
        this.kontekst = value;
    }

    /**
     * Gets the value of the operacija property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperacija() {
        return operacija;
    }

    /**
     * Sets the value of the operacija property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperacija(String value) {
        this.operacija = value;
    }

    /**
     * Gets the value of the sadrzaj property.
     * 
     * @return
     *     possible object is
     *     {@link Sadrzaj }
     *     
     */
    public Sadrzaj getSadrzaj() {
        return sadrzaj;
    }

    /**
     * Sets the value of the sadrzaj property.
     * 
     * @param value
     *     allowed object is
     *     {@link Sadrzaj }
     *     
     */
    public void setSadrzaj(Sadrzaj value) {
        this.sadrzaj = value;
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
