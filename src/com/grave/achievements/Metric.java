package com.grave.achievements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Metric {
	private static final int METRIC_COUNT = Metrics.values().length;
	public static final Metric NONE = new Metric();

	private boolean [] metrics;

	public Metric(Metrics... metrics_) {
		this.metrics = new boolean[METRIC_COUNT];
		for(Metrics metric : metrics_) {
			int i = metric.index;
			metrics[i] = true;
		}
	}

	/**
	 * Check for the presence of a single metric.
	 * @param metric The metric to search for.
	 * @return A boolean value representing whether or not this composite contains the desired metric.
	 */
	public boolean has(Metrics metric) {
		int i = metric.index;
		return metrics[i];
	}

	/**
	 * Check for the presence of multiple metrics.
	 * @param metrics_ As many metrics as you wish to search for.
	 * @return True or false depending on whether the composite contains all metrics searched for. Only returns true if composite contains all searched metrics.
	 */
	public boolean has(Metrics... metrics_) {
		boolean result = true;

		for(Metrics metric : metrics_) {
			int i = metric.index;
			result = (result && metrics[i]);
		}

		return result;
	}

	public Metric add(Metrics metric) {
		int i = metric.index;
		metrics[i] = true;
		return this;
	}

	public Metric add(Metrics... metrics_) {
		for(Metrics metric : metrics_) {
			int i = metric.index;
			metrics[i] = true;
		}

		return this;
	}

	public static Metric parse(String data) {
		Metric metric = new Metric();

		String [] tokens = data.split(",");
		for(String token : tokens) {
			if(!token.isEmpty()) {
				try {
					int index = Integer.parseInt(token);
					Metrics m = Metrics.getByIndex(index);
					metric.add(m);
				} catch(NumberFormatException nfe) {
					System.err.println("ERROR: Could not parse metric index.");
					continue;
				}
			}
		}

		return metric;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");

		List<Integer> vals = new ArrayList<Integer>();
		for(int i = 0; i < metrics.length; i++) {
			if(metrics[i]) vals.add(i);
		}

		for(int i = 0; i < vals.size(); i++) {
			builder.append(Integer.toString(vals.get(i)));
			if(i < (vals.size() - 1)) builder.append(",");
		}

		builder.append("}");
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Metric other = (Metric) obj;
        boolean equal = true;
        for(int i = 0; i < METRIC_COUNT; i++) {
        	equal = (equal && (metrics[i] == other.metrics[i]));
        }

        return equal;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(metrics);
	}
}
