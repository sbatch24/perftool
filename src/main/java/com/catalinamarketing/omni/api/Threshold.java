package com.catalinamarketing.omni.api;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Class represents a Threshold award.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "threshold")
public class Threshold extends BaseMedia {

    @XmlElement(required = true)
    protected int thresholdSequenceNumber;

    @XmlElement(required = true)
    protected int balance;

    @XmlElement(required = true)
    protected String type;

    /**
     * @return the thresholdSequenceNumber
     */
    public int getThresholdSequenceNumber() {
        return thresholdSequenceNumber;
    }

    /**
     * @param thresholdSequenceNumber the thresholdSequenceNumber to set
     */
    public void setThresholdSequenceNumber(int thresholdSequenceNumber) {
        this.thresholdSequenceNumber = thresholdSequenceNumber;
    }

    /**
     * @return the balance
     */
    public int getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(int balance) {
        this.balance = balance;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
