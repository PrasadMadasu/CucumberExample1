package util;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import dto.CarryOverBalanceDTO;

public class Java8Utils {
	
	public static Predicate<CarryOverBalanceDTO> matchedObjects(String empCode,
														        String empFullName,
														        String timeOffName) {
		return p -> (empCode.equals(p.getEmpCode()) && 
					 empFullName.equals(p.getEmpFullName()) && 
					 timeOffName.equals(p.getTimeOffName()));
	}
	
	public static List<CarryOverBalanceDTO> filterCarryOverBalanceDTOs(List<CarryOverBalanceDTO> carryOverBalanceDTOs, 
		      														   Predicate<CarryOverBalanceDTO> predicate) {
		return carryOverBalanceDTOs.stream()
								   .filter(predicate)
								   .collect(Collectors.<CarryOverBalanceDTO> toList());
	}
	
}
