
export enum MessageableMappingConfidence
{
	NONE = "NONE",
	LOW = "low",
	MEDIUM = "medium",
	HIGH = "high",
}

/**
 * convert manageable confidence level to a number representing it's order. i.e. Higher number == higher confidence
 * @param confidenceLevel
 */
export function confidenceLevelToNumber(confidenceLevel: MessageableMappingConfidence): number
{
	switch (confidenceLevel)
	{
		case MessageableMappingConfidence.NONE:
			return 0;
		case MessageableMappingConfidence.LOW:
			return 1;
		case MessageableMappingConfidence.MEDIUM:
			return 2
		case MessageableMappingConfidence.HIGH:
			return 3;
	}
}