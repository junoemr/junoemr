INSERT INTO `program` VALUES (10034,1,NULL,'OSCAR',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Bed',NULL,99999,0,0,0,0,'active',NULL,NULL,0,0,0,0,NULL,0,0,0,'no',NULL,30,1,200,1,NULL,NULL,0,0,NULL,'2016-12-14 10:10:20',0,0,NULL,NULL,NULL,0);
INSERT INTO `program_provider` VALUES (1, 10034, 999998, 2, NULL);
INSERT INTO `appointment_status` VALUES
(1,'t','To Do','#FDFEC7','#8ac5e6','starbill.gif',1,0,0,'TODO'),
(2,'T','Daysheet Printed','#FDFEC7','#8ac5e6','todo.gif',1,1,0,'DSPrt'),
(3,'H','Here','#00ee00','#95e6a3','here.gif',1,1,0,'HERE'),
(4,'P','Picked','#FFBBFF','#e6a1b8','picked.gif',1,1,0,'PICK'),
(5,'E','Empty Room','#FFFF33','#f2e291','empty.gif',1,1,0,'EmpRm'),
(6,'a','Customized 1','#897DF8','#ac9df2','1.gif',1,1,0,'CUST1'),
(7,'b','Customized 2','#897DF8','#ac9df2','2.gif',1,1,0,'CUST2'),
(8,'c','Customized 3','#897DF8','#ac9df2','3.gif',0,1,0,'CUST3'),
(9,'d','Customized 4','#897DF8','#ac9df2','4.gif',1,1,0,'CUST4'),
(10,'e','Customized 5','#897DF8','#ac9df2','5.gif',1,1,0,'CUST5'),
(11,'N','No Show','#cccccc','#d0d5e3','noshow.gif',1,0,0,'NOSHO'),
(12,'C','Cancelled','#999999','#d0d5e3','cancel.gif',1,0,0,'CAN'),
(13,'B','Billed','#3ea4e1','#8a99e6','billed.gif',1,0,0,'BILL');
UPDATE `security` SET forcePasswordReset=0 WHERE security_no=128;