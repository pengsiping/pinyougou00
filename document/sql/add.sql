SET FOREIGN_KEY_CHECKS=0;

--CREATE TABLE IF NOT EXISTS example()
CREATE TABLE `tb_analyse_pv` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `startTime` datetime DEFAULT NULL COMMENT '开始时间',
  `endTime` datetime DEFAULT NULL COMMENT '结束时间',
  `num` bigint(20) DEFAULT NULL COMMENT '访问数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;