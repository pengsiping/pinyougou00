/*
SQLyog Ultimate v11.33 (64 bit)
MySQL - 5.5.40 
*********************************************************************
*/
/*!40101 SET NAMES utf8 */;

create table `tb_user` (
	`id` bigint (20),
	`username` varchar (150),
	`password` varchar (96),
	`phone` varchar (60),
	`email` varchar (150),
	`created` datetime ,
	`updated` datetime ,
	`source_type` varchar (3),
	`nick_name` varchar (150),
	`name` varchar (150),
	`status` varchar (3),
	`head_pic` varchar (450),
	`qq` varchar (60),
	`account_balance` Decimal (11),
	`is_mobile_check` varchar (3),
	`is_email_check` varchar (3),
	`sex` varchar (3),
	`user_level` int (11),
	`points` int (11),
	`experience_value` int (11),
	`birthday` varchar (60),
	`last_login_time` datetime ,
	`province_id` varchar (60),
	`city_id` varchar (60),
	`town_id` varchar (60),
	`job` varchar (60)
); 
insert into `tb_user` (`id`, `username`, `password`, `phone`, `email`, `created`, `updated`, `source_type`, `nick_name`, `name`, `status`, `head_pic`, `qq`, `account_balance`, `is_mobile_check`, `is_email_check`, `sex`, `user_level`, `points`, `experience_value`, `birthday`, `last_login_time`, `province_id`, `city_id`, `town_id`, `job`) values('1','sunwukong','123123','1112221111',NULL,'2017-08-19 20:50:21','2017-08-19 20:50:21',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
insert into `tb_user` (`id`, `username`, `password`, `phone`, `email`, `created`, `updated`, `source_type`, `nick_name`, `name`, `status`, `head_pic`, `qq`, `account_balance`, `is_mobile_check`, `is_email_check`, `sex`, `user_level`, `points`, `experience_value`, `birthday`, `last_login_time`, `province_id`, `city_id`, `town_id`, `job`) values('2','zhubajie','4297f44b13955235245b2497399d7a93','111122',NULL,'2017-08-19 21:00:23','2017-08-19 21:00:23',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
insert into `tb_user` (`id`, `username`, `password`, `phone`, `email`, `created`, `updated`, `source_type`, `nick_name`, `name`, `status`, `head_pic`, `qq`, `account_balance`, `is_mobile_check`, `is_email_check`, `sex`, `user_level`, `points`, `experience_value`, `birthday`, `last_login_time`, `province_id`, `city_id`, `town_id`, `job`) values('3','shaheshang','96e79218965eb72c92a549dd5a330112','13900112222',NULL,'2017-08-19 22:37:44','2017-08-19 22:37:44',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
insert into `tb_user` (`id`, `username`, `password`, `phone`, `email`, `created`, `updated`, `source_type`, `nick_name`, `name`, `status`, `head_pic`, `qq`, `account_balance`, `is_mobile_check`, `is_email_check`, `sex`, `user_level`, `points`, `experience_value`, `birthday`, `last_login_time`, `province_id`, `city_id`, `town_id`, `job`) values('4','zhangsan','00b7691d86d96aebd21dd9e138f90840','17701265258',NULL,'2017-08-19 23:44:45','2017-08-19 23:44:45',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
insert into `tb_user` (`id`, `username`, `password`, `phone`, `email`, `created`, `updated`, `source_type`, `nick_name`, `name`, `status`, `head_pic`, `qq`, `account_balance`, `is_mobile_check`, `is_email_check`, `sex`, `user_level`, `points`, `experience_value`, `birthday`, `last_login_time`, `province_id`, `city_id`, `town_id`, `job`) values('5','lisi','8fbaad286e993d37b34b41749894b4a7','13401341444',NULL,'2017-08-20 11:08:29','2017-08-20 11:08:29',NULL,NULL,NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
insert into `tb_user` (`id`, `username`, `password`, `phone`, `email`, `created`, `updated`, `source_type`, `nick_name`, `name`, `status`, `head_pic`, `qq`, `account_balance`, `is_mobile_check`, `is_email_check`, `sex`, `user_level`, `points`, `experience_value`, `birthday`, `last_login_time`, `province_id`, `city_id`, `town_id`, `job`) values('6','wangwu','d41d8cd98f00b204e9800998ecf8427e','13601566766',NULL,'2017-08-20 11:09:26','2017-08-20 11:09:26',NULL,NULL,NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
insert into `tb_user` (`id`, `username`, `password`, `phone`, `email`, `created`, `updated`, `source_type`, `nick_name`, `name`, `status`, `head_pic`, `qq`, `account_balance`, `is_mobile_check`, `is_email_check`, `sex`, `user_level`, `points`, `experience_value`, `birthday`, `last_login_time`, `province_id`, `city_id`, `town_id`, `job`) values('7','zhaoliu','f379eaf3c831b04de153469d1bec345e','13669669966',NULL,'2017-08-20 12:09:27','2017-08-20 12:09:27',NULL,NULL,NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
insert into `tb_user` (`id`, `username`, `password`, `phone`, `email`, `created`, `updated`, `source_type`, `nick_name`, `name`, `status`, `head_pic`, `qq`, `account_balance`, `is_mobile_check`, `is_email_check`, `sex`, `user_level`, `points`, `experience_value`, `birthday`, `last_login_time`, `province_id`, `city_id`, `town_id`, `job`) values('8','lijialong','4297f44b13955235245b2497399d7a93','13260006290',NULL,'2017-08-20 12:23:37','2017-08-20 12:23:37',NULL,NULL,NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
insert into `tb_user` (`id`, `username`, `password`, `phone`, `email`, `created`, `updated`, `source_type`, `nick_name`, `name`, `status`, `head_pic`, `qq`, `account_balance`, `is_mobile_check`, `is_email_check`, `sex`, `user_level`, `points`, `experience_value`, `birthday`, `last_login_time`, `province_id`, `city_id`, `town_id`, `job`) values('9','zhangsanfeng','e10adc3949ba59abbe56e057f20f883e','13427946685',NULL,'2018-03-10 09:43:38','2018-03-10 09:43:38','1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'//',NULL,NULL,NULL,NULL,NULL);
insert into `tb_user` (`id`, `username`, `password`, `phone`, `email`, `created`, `updated`, `source_type`, `nick_name`, `name`, `status`, `head_pic`, `qq`, `account_balance`, `is_mobile_check`, `is_email_check`, `sex`, `user_level`, `points`, `experience_value`, `birthday`, `last_login_time`, `province_id`, `city_id`, `town_id`, `job`) values('10','zhansansan','e10adc3949ba59abbe56e057f20f883e','13005443127',NULL,'2018-04-11 12:44:31','2018-04-11 12:44:31',NULL,'zhansansan',NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
insert into `tb_user` (`id`, `username`, `password`, `phone`, `email`, `created`, `updated`, `source_type`, `nick_name`, `name`, `status`, `head_pic`, `qq`, `account_balance`, `is_mobile_check`, `is_email_check`, `sex`, `user_level`, `points`, `experience_value`, `birthday`, `last_login_time`, `province_id`, `city_id`, `town_id`, `job`) values('11','lisisi','e10adc3949ba59abbe56e057f20f883e','13888888888',NULL,'2018-05-13 16:15:13','2018-05-13 16:15:13',NULL,NULL,NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
insert into `tb_user` (`id`, `username`, `password`, `phone`, `email`, `created`, `updated`, `source_type`, `nick_name`, `name`, `status`, `head_pic`, `qq`, `account_balance`, `is_mobile_check`, `is_email_check`, `sex`, `user_level`, `points`, `experience_value`, `birthday`, `last_login_time`, `province_id`, `city_id`, `town_id`, `job`) values('12','lisiwei','e10adc3949ba59abbe56e057f20f883e','13826246091',NULL,'2018-05-13 18:38:11','2018-05-13 18:38:11',NULL,NULL,NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
insert into `tb_user` (`id`, `username`, `password`, `phone`, `email`, `created`, `updated`, `source_type`, `nick_name`, `name`, `status`, `head_pic`, `qq`, `account_balance`, `is_mobile_check`, `is_email_check`, `sex`, `user_level`, `points`, `experience_value`, `birthday`, `last_login_time`, `province_id`, `city_id`, `town_id`, `job`) values('13','lisifeng','e10adc3949ba59abbe56e057f20f883e','123213132131',NULL,'2018-07-06 17:56:29','2018-07-06 17:56:29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
insert into `tb_user` (`id`, `username`, `password`, `phone`, `email`, `created`, `updated`, `source_type`, `nick_name`, `name`, `status`, `head_pic`, `qq`, `account_balance`, `is_mobile_check`, `is_email_check`, `sex`, `user_level`, `points`, `experience_value`, `birthday`, `last_login_time`, `province_id`, `city_id`, `town_id`, `job`) values('14','wangwufeng','e10adc3949ba59abbe56e057f20f883e','18681174197',NULL,'2018-07-06 18:28:53','2018-07-06 18:28:53',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
