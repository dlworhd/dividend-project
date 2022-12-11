package com.zerobase.bdg.persist.entity;

import com.zerobase.bdg.model.Dividend;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "DIVIDEND")
@Getter
@ToString
@NoArgsConstructor
@Table(
		uniqueConstraints = // Constraint = 제약 조건 -> Unique Key가 중복되면 예외 발생
				@UniqueConstraint(
						columnNames = { "companyId" , "date"} // 복합 컬럼
				)
)
public class DividendEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long companyId;
	private LocalDateTime date;
	private String dividend;

	public DividendEntity(Long companyId, Dividend dividend) {
		this.companyId = companyId;
		this.date = dividend.getDate();
		this.dividend = dividend.getDividend();
	}
}
