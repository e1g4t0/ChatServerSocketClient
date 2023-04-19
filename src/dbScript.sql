create database SCProject;
use SCProject;


create table chat_history(
	id int auto_increment primary key,
    username varchar(50),
    message text,
    message_datetime datetime
);

-- requests 
select * from (
	select * from chat_history
	order by id desc
	limit 10) sub
order by sub.id;