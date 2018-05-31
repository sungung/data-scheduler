package com.sungung.scheduler.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = JobGroup.Builder.class)
public class JobGroup implements Cloneable {
	
	@JsonProperty
	private String name;
	@JsonProperty
	private String description;
	@JsonProperty
	private String node;
	
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getNode() {
		return node;
	}	

	@JsonPOJOBuilder(buildMethodName = "build")
	public static class Builder{
		@JsonProperty
		private String name;
		@JsonProperty
		private String description;
		@JsonProperty
		private String node;
		public Builder name(String name){
			this.name = name;
			return this;
		}
		public Builder description(String description){
			this.description = description;
			return this;
		}
		public Builder node(String node){
			this.node = node;
			return this;
		}
		public JobGroup build(){
			JobGroup jobGroup = new JobGroup();
			jobGroup.name = this.name;
			jobGroup.description = this.description;
			jobGroup.node = this.node;
			return jobGroup;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobGroup other = (JobGroup) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JobGroup [name=" + name + ", description=" + description + ", node=" + node + "]";
	}
	
	protected Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
	
}
