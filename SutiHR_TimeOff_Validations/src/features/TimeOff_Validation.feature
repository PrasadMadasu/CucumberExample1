Feature: SutiHR application TimeOff balance validations
	Login as HR admin and verify the employees TimeOff balances validations
	
	Scenario: Login as HR admin and get the employee TimeOff balances
		Given go to SutiHR application login page
		And login as HR admin username and password
		Then read Time Off Balances report data
		Then read Carry Over Balances report data
		And read the employees Time Off balances
		Then store all details into xlsx file
		Then read Time Off details
		Then sign out the SutiHR application