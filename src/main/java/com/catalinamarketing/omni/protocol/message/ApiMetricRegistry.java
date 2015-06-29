package com.catalinamarketing.omni.protocol.message;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="apiMetric")
public class ApiMetricRegistry {
	private String name;
	@XmlElementWrapper(name="metricList")
	@XmlElement(name="metric")
	private List<Metric> metrics;
	
	public ApiMetricRegistry(){
		
	}
	
	public ApiMetricRegistry(String name) {
		this.setName(name);
		this.metrics = new ArrayList<Metric>();
	}
	
	public void addMetric(Metric metric) {
		this.metrics.add(metric);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
