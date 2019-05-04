-- MySQL dump 10.13  Distrib 5.7.21, for Linux (x86_64)
--
-- Host: 120.79.74.63    Database: datedb
-- ------------------------------------------------------
-- Server version	5.7.18

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application` (
  `application_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请ID',
  `applicant` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '申请者',
  `post_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求的帖子ID',
  `phone_no` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号码',
  `wechat_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信号码',
  `qq_no` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'qq号码',
  `email` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮件地址',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '留言补充',
  `status` tinyint(4) DEFAULT '0' COMMENT '0:未审核,1:已通过,2:未通过',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `valid` tinyint(4) DEFAULT '1' COMMENT '0无效,1有效\n',
  PRIMARY KEY (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application`
--

LOCK TABLES `application` WRITE;
/*!40000 ALTER TABLE `application` DISABLE KEYS */;
INSERT INTO `application` VALUES ('3a932dbc17284b5c92c55c57b4363be1','2016302580263','1002b6960dd949239b44bbcd9ea5de3b','12345678912',NULL,'123456789',NULL,'go!go!go!',1,'2019-05-02 16:05:52','2019-05-02 16:04:17',1),('e0584ae5167e4b1f88e15a13fb8dfd2b','2016302580263','6d79b8494ff14cd3a460ce84f840c75d','12345678912',NULL,'123456789',NULL,'go!go!go!',0,NULL,'2019-05-02 21:52:01',1),('f75da03640144535be804e8dbdf83f5b','2016302580263','67b625b8c6094ef5bc16f448930637de','12345678912',NULL,'123456789',NULL,'go!go!go!',0,NULL,'2019-05-04 15:12:53',1);
/*!40000 ALTER TABLE `application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification` (
  `notification_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知ID',
  `receiver` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收者',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '通知内容',
  `application_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '申请ID',
  `post_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '帖子ID',
  `status` int(11) DEFAULT '0' COMMENT '0未读,1已读',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `valid` tinyint(4) DEFAULT '1' COMMENT '0无效,1有效',
  `type` tinyint(4) DEFAULT NULL COMMENT '通知类型 0表示针对post 1表示针对Application\n'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES ('ec5a052695db4e859963c11abd460929','2016302580263','有人向您的帖子发送了申请','98a60061544c4a799f24b5bf88ae2f91','1002b6960dd949239b44bbcd9ea5de3b',0,'2019-05-02 15:07:59',NULL,1,0),('826a7b71918c4cb4b3f0e5e38a0d0ed5','2016302580263','有人向您的帖子发送了申请','c339e4d1132f45f19ccb9aadc7c29833','1002b6960dd949239b44bbcd9ea5de3b',0,'2019-05-02 15:08:00',NULL,1,0),('e073aa90057e49f9b2dd5ca8d8fff190','2016302580263','您的申请已通过','98a60061544c4a799f24b5bf88ae2f91','1002b6960dd949239b44bbcd9ea5de3b',0,'2019-05-02 15:14:03',NULL,1,1),('1f06d71ef415481eb335e696249bd38d','2016302580263','您的申请未通过','c339e4d1132f45f19ccb9aadc7c29833','1002b6960dd949239b44bbcd9ea5de3b',0,'2019-05-02 15:15:57',NULL,1,1),('130ac8162a704fd58ccd5dd4084294b8','2016302580263','有人向您的帖子发送了申请','3a932dbc17284b5c92c55c57b4363be1','1002b6960dd949239b44bbcd9ea5de3b',0,'2019-05-02 16:04:17',NULL,1,0),('4029e995b502441abf50e6338a837506','2016302580263','您的申请已通过','3a932dbc17284b5c92c55c57b4363be1','1002b6960dd949239b44bbcd9ea5de3b',0,'2019-05-02 16:05:52',NULL,1,1),('8c58d51e03cb42f0be8da0e1e5df5a25','2016302580263','有人向您的帖子发送了申请','e0584ae5167e4b1f88e15a13fb8dfd2b','6d79b8494ff14cd3a460ce84f840c75d',1,'2019-05-02 21:52:01',NULL,1,0);
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post`
--

DROP TABLE IF EXISTS `post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `post` (
  `post_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'postID',
  `title` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '正文',
  `poster` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发帖人',
  `category` tinyint(4) DEFAULT NULL COMMENT '类型 0学习 1娱乐 2其他',
  `tag` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签,用来构建推荐系统',
  `max_num` int(11) NOT NULL COMMENT '最大人数',
  `cur_num` int(11) DEFAULT '1' COMMENT '当前人数',
  `images` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片链接({1,})',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `valid` tinyint(4) DEFAULT '1' COMMENT '0无效,1有效',
  `status` tinyint(4) DEFAULT '0' COMMENT '0:正常,1:已满,2:已关闭',
  `address` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `area` tinyint(4) DEFAULT NULL COMMENT '地区 0文理学部 1信息学部 2工学部\n3医学部 4校外',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `snowflake_id` bigint(20) DEFAULT NULL,
  `snowflake_id_str` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category_str` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `area_str` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`post_id`),
  UNIQUE KEY `post_snowflake_id_uindex` (`snowflake_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post`
--

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
INSERT INTO `post` VALUES ('1002b6960dd949239b44bbcd9ea5de3b','看电影:妇联4','这周末去电影院看电影:妇联4','2016302580263',1,'电影,妇联',4,3,NULL,'2019-05-02 14:21:17','2019-05-02 16:05:52',1,0,'电影院',4,'2019-05-03 15:23:02',7812889067902877696,'7812889067902877696','娱乐','校外'),('24da2202e15448889d1b46f76bf03dad','图书馆约学习4','这周末去图书馆一起学习','2016302580263',0,'图书馆,学习',2,1,NULL,'2019-05-02 14:20:10',NULL,1,0,'图书馆',1,'2019-05-03 15:23:06',7812892803193126912,'7812892803193126912','学习','信息学部'),('26049c541be945bc94c3568b91d66719','图书馆约学习1','这周末去图书馆一起学习','2016302580263',0,'图书馆,学习',2,1,NULL,'2019-05-02 00:18:48',NULL,1,0,'图书馆',1,'2019-05-03 15:23:07',7812892936110882816,'7812892936110882816','学习','信息学部'),('67b625b8c6094ef5bc16f448930637de','看电影:妇联4-3','这周末去电影院看电影:妇联4','2016302580263',1,'电影,妇联',4,1,'http://120.79.74.63:8080/group1/M00/00/00/rBE_2lzNO1iARoZRAAAzrgFVwQs141.jpg,','2019-05-04 14:46:21',NULL,1,0,'电影院',4,'2019-05-04 14:45:44',7812906926134804480,'7812906926134804480','娱乐','校外'),('6c77cfcae7c2416987998bc38f1f7ee6','看电影:妇联4','这周末去电影院看电影:妇联4','2016302580263',1,'电影,妇联',4,1,NULL,'2019-05-02 14:21:21',NULL,1,0,'电影院',4,'2019-05-03 15:23:04',7812892936110882817,'7812892936110882817','娱乐','校外'),('6d79b8494ff14cd3a460ce84f840c75d','看电影:妇联4-2','这周末去电影院看电影:妇联4','2016302580263',1,'电影,妇联',4,1,'http://120.79.74.63:8080/group1/M00/00/00/rBE_2lzK_A6ALOjJAAAzrgFVwQs611.jpg','2019-05-02 21:49:02',NULL,1,0,'电影院',4,'2019-05-03 15:22:53',7812892936110882818,'7812892936110882818','娱乐','校外'),('c3bc0d2865f344ab989e6e4c49bb4574','看电影:妇联4-1','这周末去电影院看电影:妇联4','2016302580263',1,'电影,妇联',4,1,NULL,'2019-05-02 21:44:39',NULL,1,0,'电影院',4,'2019-05-03 15:22:58',7812892936110882819,'7812892936110882819','娱乐','校外'),('ffccd1878de046c283376116a124e24b','看电影:妇联4-1','这周末去电影院看电影:妇联4','2016302580263',1,'电影,妇联',4,1,NULL,'2019-05-02 21:44:40',NULL,1,0,'电影院',4,'2019-05-03 15:23:01',7812892936110882820,'7812892936110882820','娱乐','校外');
/*!40000 ALTER TABLE `post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_member`
--

DROP TABLE IF EXISTS `post_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `post_member` (
  `post_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帖子ID\n',
  `member_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '成员ID(学号)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `valid` tinyint(4) DEFAULT '1' COMMENT '0无效,1有效',
  PRIMARY KEY (`post_id`,`member_id`),
  UNIQUE KEY `post_member_post_id_member_id_uindex` (`post_id`,`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子参与者表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_member`
--

LOCK TABLES `post_member` WRITE;
/*!40000 ALTER TABLE `post_member` DISABLE KEYS */;
INSERT INTO `post_member` VALUES ('1002b6960dd949239b44bbcd9ea5de3b','2016302580263','2019-05-02 16:05:52',NULL,1);
/*!40000 ALTER TABLE `post_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report` (
  `report_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '举报ID',
  `reporter` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '举报者',
  `target` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '举报的帖子',
  `category` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '举报原因类型',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '补充内容',
  `status` tinyint(4) DEFAULT NULL COMMENT '0:未审核,1:已通过,2:未通过',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `valid` tinyint(4) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='举报表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report`
--

LOCK TABLES `report` WRITE;
/*!40000 ALTER TABLE `report` DISABLE KEYS */;
/*!40000 ALTER TABLE `report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `sno` char(13) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学号',
  `username` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `phone_no` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电子邮件地址',
  `auth` tinyint(4) DEFAULT '0' COMMENT '是否认证,0未认证,1已认证',
  `avatar` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT 'http://120.79.74.63:8080/group1/M00/00/00/rBE_2lzK84OATckVAAAzrgFVwQs261.jpg' COMMENT '用户头像',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `valid` tinyint(4) DEFAULT '1' COMMENT '是否有效,0无效,1有效',
  `credit` int(11) DEFAULT '0' COMMENT '信誉分',
  `sno_long` mediumtext COLLATE utf8mb4_unicode_ci COMMENT 'long',
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('2015302580284','velor2012','123','15927424626','228321423543@qq.com',0,'http://120.79.74.63:8080/group1/M00/00/00/rBE_2lzK84OATckVAAAzrgFVwQs261.jpg','2019-05-03 12:52:48','2019-05-03 21:43:57',1,0,'2015302580284'),('2016302580263','swh','123456','11111111111','4321@whu.edu.cn',0,'http://120.79.74.63:8080/group1/M00/00/00/rBE_2lzNMWGAVir1AAAzrgFVwQs736.jpg','2019-05-01 16:45:01',NULL,1,0,'2016302580263'),('2016302580284','velor2012','123456','15927424626','2281675608@qq.com',0,'http://120.79.74.63:8080/group1/M00/00/00/rBE_2lzK84OATckVAAAzrgFVwQs261.jpg','2019-05-03 12:48:56',NULL,1,0,'2016302580284'),('2134566','1321e','123456','15927424626','22@qq.com',0,'http://120.79.74.63:8080/group1/M00/00/00/rBE_2lzK84OATckVAAAzrgFVwQs261.jpg','2019-05-03 12:10:31',NULL,1,0,'2134566');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_visit_action`
--

DROP TABLE IF EXISTS `user_visit_action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_visit_action` (
  `action_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `search_keyword` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '搜索关键词',
  `click_post_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '点击的帖子ID',
  `apply_post_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '申请的帖子ID',
  `valid` tinyint(4) DEFAULT '1',
  `snowflake_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_visit_action`
--

LOCK TABLES `user_visit_action` WRITE;
/*!40000 ALTER TABLE `user_visit_action` DISABLE KEYS */;
INSERT INTO `user_visit_action` VALUES ('b86e2e1f94c24f9b967cd245db527467','2016302580263','2019-05-02 00:27:29','学习',NULL,NULL,1,NULL),('9ef8d49e4d624898b1d464dded07b553','2016302580263','2019-05-02 00:27:38','学习',NULL,NULL,1,NULL),('4e5e1c8d88304987866f58ec5268bc6f','2016302580263','2019-05-02 00:27:46','学习1',NULL,NULL,1,NULL),('7a9bc72dce6d4ff4aaf15741455d9ae4','2016302580263','2019-05-02 00:28:02','学习1',NULL,NULL,1,NULL),('e6cc5a09cddb443b90deb865d7f061a6','2016302580263','2019-05-02 00:28:21','去图书馆学习2',NULL,NULL,1,NULL),('2ac9b40fb94343ad98fd67bb12a563a3','2016302580263','2019-05-02 00:28:29','去图书馆学习',NULL,NULL,1,NULL),('b3a522e4582e44fbac0a676c984b67da','2016302580263','2019-05-02 00:28:35','图书馆学习',NULL,NULL,1,NULL),('3ffd113118694060a7c4d9f3f6602398','2016302580263','2019-05-02 00:28:39','图书馆',NULL,NULL,1,NULL),('3996c00b7d3843959aa50d6053488f48','2016302580263','2019-05-02 00:28:58','去图书馆学习2',NULL,NULL,1,NULL),('44b03790a7654d88aa6514bbc883f6ac','2016302580263','2019-05-02 00:29:07','去图书馆学习',NULL,NULL,1,NULL),('0693df41fa8f406d8acba3653eedc8dd','2016302580263','2019-05-02 00:29:11','图书馆学习',NULL,NULL,1,NULL),('dfc70bb988304524bee6128c6e3e5852','2016302580263','2019-05-02 00:29:15','图书馆',NULL,NULL,1,NULL),('d27f5ae1c1b04e30945fe64c36b615b4','2016302580263','2019-05-02 00:29:40','图书馆',NULL,NULL,1,NULL),('f48008d498ca4007b288e368cb52b9a8','2016302580263','2019-05-02 00:29:55','去图书馆',NULL,NULL,1,NULL),('725d2dc711f24b12823489bc041b34bd','2016302580263','2019-05-02 00:30:05','图书馆学习',NULL,NULL,1,NULL),('a3d33968dfab4baebe346f737427617d','2016302580263','2019-05-02 00:32:40','图书馆',NULL,NULL,1,NULL),('b500f129af3b44aeae2f30736356a077','2016302580263','2019-05-02 00:32:49','图书馆',NULL,NULL,1,NULL),('5cdadba110ae41a587b9ce90ce80b9f4','2016302580263','2019-05-02 00:34:23',NULL,'d1bbb6b155394748a2efc6b8ada3370f',NULL,1,NULL),('4352345609a0442f919e21acba6090dc','2016302580263','2019-05-02 14:21:50','图书馆学习',NULL,NULL,1,NULL),('5e1fac75d8c7499ba0d5d9b50e2d62f9','2016302580263','2019-05-02 14:22:18','图书馆学习',NULL,NULL,1,NULL),('e614072c3eb54b59bb5663f74c8cd33d','2016302580263','2019-05-02 14:22:18','图书馆学习',NULL,NULL,1,NULL),('7ddc5e1820364be3a6c01810b360064b','2016302580263','2019-05-02 14:22:18','图书馆学习',NULL,NULL,1,NULL),('ceed1eccbe1448a1991bf7fbed4f21d1','2016302580263','2019-05-02 14:22:52','图书馆学习',NULL,NULL,1,NULL),('254bb8c37be3413a9a90ee6e7b3ed820','2016302580263','2019-05-02 14:40:01',NULL,NULL,'1002b6960dd949239b44bbcd9ea5de3b',1,7812889067902877696),('cdbb9f4655184a858edbf9d4ac393010','2016302580263','2019-05-02 14:47:58',NULL,'d1bbb6b155394748a2efc6b8ada3370f',NULL,1,NULL),('bee43fd0e5bd4e069b47941e20f89557','2016302580263','2019-05-02 14:48:00',NULL,'d1bbb6b155394748a2efc6b8ada3370f',NULL,1,NULL),('6c898a86ab6641ab8ebee1fa0fe589ba','2016302580263','2019-05-02 14:48:00',NULL,'d1bbb6b155394748a2efc6b8ada3370f',NULL,1,NULL),('df3a27f8a1104f099c5782bdfd0f555d','2016302580263','2019-05-02 14:54:40',NULL,NULL,'1002b6960dd949239b44bbcd9ea5de3b',1,7812889067902877696),('12424cf3cb1245f48c40d1c9dcd5be0f','2016302580263','2019-05-02 15:07:59',NULL,NULL,'1002b6960dd949239b44bbcd9ea5de3b',1,7812889067902877696),('c7417cb062974f869e825f557b3f42f1','2016302580263','2019-05-02 15:08:00',NULL,NULL,'1002b6960dd949239b44bbcd9ea5de3b',1,7812889067902877696),('5a717cdd4c714cc197458c5baae6dab8','2016302580263','2019-05-02 16:04:17',NULL,NULL,'1002b6960dd949239b44bbcd9ea5de3b',1,NULL),('93ce60c78e4e478a8f4b4ed63e607fc2','2016302580263','2019-05-02 21:48:06','电影',NULL,NULL,1,NULL),('710c1e31f0e946a5b99ed2273ee62725','2016302580263','2019-05-02 21:52:01',NULL,NULL,'6d79b8494ff14cd3a460ce84f840c75d',1,7812892936110882818),('1330a37e08694c61acd5667f7bc1efa2','2015302580284','2019-05-03 21:15:14','图书馆',NULL,NULL,1,NULL),('83d70c3619614209a98c845bfd8e231e','2015302580284','2019-05-03 21:15:34','图书',NULL,NULL,1,NULL),('e59b14c397204d3188a9da0833b93b5b','2015302580284','2019-05-03 21:17:13','图书馆',NULL,NULL,1,NULL),('ff3aeb7d5ebf4c4088b8927ce4c42c65','2015302580284','2019-05-03 21:17:22','图书馆',NULL,NULL,1,NULL),('99bbdf4fd3e6450fb38542f437eb7a4d','2015302580284','2019-05-03 21:19:20','图书馆',NULL,NULL,1,NULL),('47f43f3a96b54c6da3ed47cff7ec59ee','2015302580284','2019-05-03 21:22:06','电影',NULL,NULL,1,NULL),('863c0761169f45a4a722a29405f21a45','2015302580284','2019-05-03 21:23:00','图书馆',NULL,NULL,1,NULL),('1a9f20c6789f4ea6bc86e8b857adb0b4','2016302580263','2019-05-03 21:45:56','电影',NULL,NULL,1,NULL),('0e8ee8af97c646b0b04b4a019fa71516','2016302580263','2019-05-03 21:46:11','学习',NULL,NULL,1,NULL),('c8aa730ff7c045988b455b0cfb080115','2016302580263','2019-05-03 21:46:19','电影',NULL,NULL,1,NULL),('3f12126fb3a94a6ba0f4dde29156e44b','2015302580284','2019-05-03 21:49:17','电影',NULL,NULL,1,NULL),('f0da94755416465cbcca75de3390d3fa','2015302580284','2019-05-03 21:55:33','妇联',NULL,NULL,1,NULL),('2e734d5d76df49a6a9cd56e5355a8631','2015302580284','2019-05-03 21:55:36','妇联',NULL,NULL,1,NULL),('8a8286596f2c4e9191756d2ce363cfc2','2016302580263','2019-05-04 15:11:29','电影',NULL,NULL,1,NULL),('1db1a3b429da4cd5a0382523d4bb022c','2016302580263','2019-05-04 15:11:38','学习',NULL,NULL,1,NULL),('12a598a966f343b1b20fc7d1ec7a8dda','2016302580263','2019-05-04 15:12:53',NULL,NULL,'67b625b8c6094ef5bc16f448930637de',1,7812906926134804480);
/*!40000 ALTER TABLE `user_visit_action` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-05-04 17:15:35
