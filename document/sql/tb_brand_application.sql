/*
SQLyog Ultimate v11.33 (64 bit)
MySQL - 5.5.40 : Database - pinyougou
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`pinyougou` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `pinyougou`;

/*Table structure for table `tb_brand_application` */

DROP TABLE IF EXISTS `tb_brand_application`;

CREATE TABLE `tb_brand_application` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '品牌名称',
  `first_char` varchar(1) DEFAULT NULL COMMENT '品牌首字母',
  `sellerId` varchar(100) DEFAULT NULL COMMENT '公司ID',
  `sellerName` varchar(80) DEFAULT NULL COMMENT '公司名',
  `status` varchar(1) DEFAULT '0' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `tb_brand_application` */

insert  into `tb_brand_application`(`id`,`name`,`first_char`,`sellerId`,`sellerName`,`status`) values (1,'联想','L','qiandu','千度公司','1'),(2,'华为','H','qiandu','千度公司','0');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
