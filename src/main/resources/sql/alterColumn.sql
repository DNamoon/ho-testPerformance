-- ALTER TABLE [테이블명] MODIFY [컬럼명] [변경할컬럼타입]
alter table performance_schedule MODIFY performance_status varchar(30);

alter table reservation MODIFY reservation_status varchar(30);