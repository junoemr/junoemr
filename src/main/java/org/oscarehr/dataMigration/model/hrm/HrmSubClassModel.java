package org.oscarehr.dataMigration.model.hrm;

import lombok.Data;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import java.time.LocalDateTime;

@Data
public class HrmSubClassModel extends AbstractTransientModel
{
	private Integer id;

	private Integer hrmCategoryId;

	private String facilityNumber;

	private String className;

	private String subClassName;

	private String accompanyingSubClassName;

	private LocalDateTime disabledAt;

	public boolean isDisabled()
	{
		return this.disabledAt == null;
	}
}