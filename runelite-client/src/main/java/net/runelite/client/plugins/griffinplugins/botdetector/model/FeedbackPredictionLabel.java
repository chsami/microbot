/*
 * Copyright (c) 2021, Ferrariic, Seltzer Bro, Cyborger1
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.griffinplugins.botdetector.model;

import java.util.Objects;
import lombok.Value;
import org.apache.commons.text.WordUtils;

@Value
public class FeedbackPredictionLabel
{
	String label;
	String normalizedLabel;
	FeedbackValue feedbackValue;
	Double labelConfidence;

	public FeedbackPredictionLabel(String label, Double labelConfidence, FeedbackValue feedbackValue)
	{
		this.label = label;
		this.normalizedLabel = normalizeLabel(label);
		this.labelConfidence = labelConfidence;
		this.feedbackValue = feedbackValue;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}

		if (o instanceof FeedbackPredictionLabel)
		{
			FeedbackPredictionLabel that = (FeedbackPredictionLabel) o;
			return Objects.equals(label, that.label)
				&& Objects.equals(labelConfidence, that.labelConfidence)
				&& Objects.equals(feedbackValue, that.feedbackValue);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return (label != null ? label.hashCode() : 0)
			+ (labelConfidence != null ? labelConfidence.hashCode() : 0)
			+ (feedbackValue != null ? feedbackValue.hashCode() : 0);
	}

	@Override
	public String toString()
	{
		return normalizedLabel;
	}

	/**
	 * Normalizes the given prediction label by separating word
	 * with spaces and making each word capitalized.
	 * @param label The label to normalize.
	 * @return The normalized label.
	 */
	public static String normalizeLabel(String label)
	{
		if (label == null)
		{
			return null;
		}

		return WordUtils.capitalize(label.replace('_', ' ').trim(), ' ');
	}
}
