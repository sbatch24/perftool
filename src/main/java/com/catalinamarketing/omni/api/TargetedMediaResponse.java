package com.catalinamarketing.omni.api;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="media")
public class TargetedMediaResponse {
	
	@XmlElementWrapper(name="directDeposits")
	private List<DirectDeposit> directDeposit;
	
	@XmlElementWrapper(name = "thresholds")
    @XmlElement(name = "threshold")
    protected List<Threshold> thresholds;

    @XmlElementWrapper(name = "stringPrints")
    @XmlElement(name = "stringPrint")
    protected List<StringPrint> stringPrints;
    
    public TargetedMediaResponse() {
	}
	
	public List<DirectDeposit> getDirectDeposits() {
		if(directDeposit == null) {
			directDeposit = new ArrayList<DirectDeposit>();
		}
		return directDeposit;
	}

	public void setDirectDeposits(List<DirectDeposit> directDeposits) {
		this.directDeposit = directDeposits;
	}

	 /**
     * @return list of thresholds
     */
    public List<Threshold> getThresholds() {
        if (thresholds == null) {
            this.thresholds = new ArrayList<>();
        }
        return thresholds;
    }

    /**
     * @return list of string prints
     */
    public List<StringPrint> getStringPrints() {
        if (stringPrints == null) {
            this.stringPrints = new ArrayList<>();
        }
        return stringPrints;
    }
	
}
