package com.criconline.actions;

public class InfoAction extends TableServerAction {

	private double bet;
	private double pot;
	private double rake;
	private boolean canRaise;
	private double raise;

	public InfoAction(double bet, double pot, double rake,
			boolean canRaise, double raise) {
		super(TABLE_INFO);
		this.bet = bet;
		this.pot = pot;
		this.rake = rake;
		this.canRaise = canRaise;
		this.raise = raise;
	}

	public double getBet() { return bet; }

	public double getPot() { return pot; }

	public double getRake() { return rake; }

	public boolean getCanRaise() { return canRaise; }

	public double getRaise() { return raise; }

}
