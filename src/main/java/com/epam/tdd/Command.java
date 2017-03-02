package com.epam.tdd;

public class Command {
	private Money first;
	private Money second;
	private Money result;
	private boolean isAddOperation;
	private boolean hasTarget;
	
	public Command(Money first, Money second){
		this(first, second, null, true);
	}
	
	public Command(Money first, Money second, Money result,  boolean isAdd){
		this.first = first;
		this.second = second;
		this.result = result;
		this.isAddOperation = isAdd;
		
		this.hasTarget = (this.result != null);
	}
	
	
	public String getQuestionStatement(){
		StringBuffer buf = new StringBuffer();
		
		buf.append(Float.toString(first.getVlaue())).append(first.getCurrency());
		buf.append(" ").append(this.isAddOperation ? "+": "-").append(" ");
		buf.append(Float.toString(second.getVlaue())).append(second.getCurrency());
		buf.append(" = ");
		if(this.hasTarget){
			buf.append(" ? ").append(this.result.getCurrency());
		}
		
		return buf.toString();
	}

	public Money first() {
		return first;
	}

	public Money second() {
		return second;
	}

	public Money result() {
		return result;
	}

	public boolean isAddOperation() {
		return isAddOperation;
	}

	public boolean hasTarget() {
		return hasTarget;
	}
	
	public boolean isSourceCurrDifferent(){
		return this.first.getCurrency() != this.second.getCurrency();
	}
	
	public boolean isTargetCurrDifferent(){
		if(hasTarget){
			return first.getCurrency() != result.getCurrency()
					|| second.getCurrency() != result.getCurrency();
		}
		
		return false;
	}
	
}
