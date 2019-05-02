-- MySQL dump 10.13  Distrib 5.7.21, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: datedb
-- ------------------------------------------------------
-- Server version	5.7.23

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
INSERT INTO `application` VALUES ('3a932dbc17284b5c92c55c57b4363be1','2016302580263','1002b6960dd949239b44bbcd9ea5de3b','12345678912',NULL,'123456789',NULL,'go!go!go!',1,'2019-05-02 16:05:52','2019-05-02 16:04:17',1);
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
  `valid` tinyint(4) DEFAULT '1' COMMENT '0无效,1有效'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES ('ec5a052695db4e859963c11abd460929','2016302580263','有人向您的帖子发送了申请','98a60061544c4a799f24b5bf88ae2f91','1002b6960dd949239b44bbcd9ea5de3b',0,'2019-05-02 15:07:59',NULL,1),('826a7b71918c4cb4b3f0e5e38a0d0ed5','2016302580263','有人向您的帖子发送了申请','c339e4d1132f45f19ccb9aadc7c29833','1002b6960dd949239b44bbcd9ea5de3b',0,'2019-05-02 15:08:00',NULL,1),('e073aa90057e49f9b2dd5ca8d8fff190','2016302580263','您的申请已通过','98a60061544c4a799f24b5bf88ae2f91','1002b6960dd949239b44bbcd9ea5de3b',0,'2019-05-02 15:14:03',NULL,1),('1f06d71ef415481eb335e696249bd38d','2016302580263','您的申请未通过','c339e4d1132f45f19ccb9aadc7c29833','1002b6960dd949239b44bbcd9ea5de3b',0,'2019-05-02 15:15:57',NULL,1),('130ac8162a704fd58ccd5dd4084294b8','2016302580263','有人向您的帖子发送了申请','3a932dbc17284b5c92c55c57b4363be1','1002b6960dd949239b44bbcd9ea5de3b',0,'2019-05-02 16:04:17',NULL,1),('4029e995b502441abf50e6338a837506','2016302580263','您的申请已通过','3a932dbc17284b5c92c55c57b4363be1','1002b6960dd949239b44bbcd9ea5de3b',0,'2019-05-02 16:05:52',NULL,1);
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
  `category` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类型',
  `tag` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签,用来构建推荐系统',
  `max_num` int(11) NOT NULL COMMENT '最大人数',
  `cur_num` int(11) DEFAULT '1' COMMENT '当前人数',
  `images` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片链接({1,})',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `valid` tinyint(4) DEFAULT '1' COMMENT '0无效,1有效',
  `status` tinyint(4) DEFAULT '0' COMMENT '0:正常,1:已满,2:已关闭',
  `address` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post`
--

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
INSERT INTO `post` VALUES ('1002b6960dd949239b44bbcd9ea5de3b','看电影:妇联4','这周末去电影院看电影:妇联4','2016302580263','娱乐','电影,妇联',4,3,NULL,'2019-05-02 14:21:17','2019-05-02 16:05:52',1,0,'电影院'),('24da2202e15448889d1b46f76bf03dad','图书馆约学习4','这周末去图书馆一起学习','2016302580263','学习','图书馆,学习',2,1,NULL,'2019-05-02 14:20:10',NULL,1,0,'信息学部图书馆'),('26049c541be945bc94c3568b91d66719','图书馆约学习1','这周末去图书馆一起学习','2016302580263','学习','图书馆,学习',2,1,NULL,'2019-05-02 00:18:48',NULL,1,0,'信息学部图书馆'),('6c77cfcae7c2416987998bc38f1f7ee6','看电影:妇联4','这周末去电影院看电影:妇联4','2016302580263','娱乐','电影,妇联',4,1,NULL,'2019-05-02 14:21:21',NULL,1,0,'电影院');
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
  `avatar` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `valid` tinyint(4) DEFAULT '1' COMMENT '是否有效,0无效,1有效',
  `credit` int(11) DEFAULT '0' COMMENT '信誉分',
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('2016302580263','swh','123456','11111111111','4321@whu.edu.cn',0,NULL,'2019-05-01 16:45:01',NULL,1,0);
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
  `valid` tinyint(4) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_visit_action`
--

LOCK TABLES `user_visit_action` WRITE;
/*!40000 ALTER TABLE `user_visit_action` DISABLE KEYS */;
INSERT INTO `user_visit_action` VALUES ('b86e2e1f94c24f9b967cd245db527467','2016302580263','2019-05-02 00:27:29','学习',NULL,NULL,1),('9ef8d49e4d624898b1d464dded07b553','2016302580263','2019-05-02 00:27:38','学习',NULL,NULL,1),('4e5e1c8d88304987866f58ec5268bc6f','2016302580263','2019-05-02 00:27:46','学习1',NULL,NULL,1),('7a9bc72dce6d4ff4aaf15741455d9ae4','2016302580263','2019-05-02 00:28:02','学习1',NULL,NULL,1),('e6cc5a09cddb443b90deb865d7f061a6','2016302580263','2019-05-02 00:28:21','去图书馆学习2',NULL,NULL,1),('2ac9b40fb94343ad98fd67bb12a563a3','2016302580263','2019-05-02 00:28:29','去图书馆学习',NULL,NULL,1),('b3a522e4582e44fbac0a676c984b67da','2016302580263','2019-05-02 00:28:35','图书馆学习',NULL,NULL,1),('3ffd113118694060a7c4d9f3f6602398','2016302580263','2019-05-02 00:28:39','图书馆',NULL,NULL,1),('3996c00b7d3843959aa50d6053488f48','2016302580263','2019-05-02 00:28:58','去图书馆学习2',NULL,NULL,1),('44b03790a7654d88aa6514bbc883f6ac','2016302580263','2019-05-02 00:29:07','去图书馆学习',NULL,NULL,1),('0693df41fa8f406d8acba3653eedc8dd','2016302580263','2019-05-02 00:29:11','图书馆学习',NULL,NULL,1),('dfc70bb988304524bee6128c6e3e5852','2016302580263','2019-05-02 00:29:15','图书馆',NULL,NULL,1),('d27f5ae1c1b04e30945fe64c36b615b4','2016302580263','2019-05-02 00:29:40','图书馆',NULL,NULL,1),('f48008d498ca4007b288e368cb52b9a8','2016302580263','2019-05-02 00:29:55','去图书馆',NULL,NULL,1),('725d2dc711f24b12823489bc041b34bd','2016302580263','2019-05-02 00:30:05','图书馆学习',NULL,NULL,1),('a3d33968dfab4baebe346f737427617d','2016302580263','2019-05-02 00:32:40','图书馆',NULL,NULL,1),('b500f129af3b44aeae2f30736356a077','2016302580263','2019-05-02 00:32:49','图书馆',NULL,NULL,1),('5cdadba110ae41a587b9ce90ce80b9f4','2016302580263','2019-05-02 00:34:23',NULL,'d1bbb6b155394748a2efc6b8ada3370f',NULL,1),('4352345609a0442f919e21acba6090dc','2016302580263','2019-05-02 14:21:50','图书馆学习',NULL,NULL,1),('5e1fac75d8c7499ba0d5d9b50e2d62f9','2016302580263','2019-05-02 14:22:18','图书馆学习',NULL,NULL,1),('e614072c3eb54b59bb5663f74c8cd33d','2016302580263','2019-05-02 14:22:18','图书馆学习',NULL,NULL,1),('7ddc5e1820364be3a6c01810b360064b','2016302580263','2019-05-02 14:22:18','图书馆学习',NULL,NULL,1),('ceed1eccbe1448a1991bf7fbed4f21d1','2016302580263','2019-05-02 14:22:52','图书馆学习',NULL,NULL,1),('254bb8c37be3413a9a90ee6e7b3ed820','2016302580263','2019-05-02 14:40:01',NULL,NULL,'1002b6960dd949239b44bbcd9ea5de3b',1),('cdbb9f4655184a858edbf9d4ac393010','2016302580263','2019-05-02 14:47:58',NULL,'d1bbb6b155394748a2efc6b8ada3370f',NULL,1),('bee43fd0e5bd4e069b47941e20f89557','2016302580263','2019-05-02 14:48:00',NULL,'d1bbb6b155394748a2efc6b8ada3370f',NULL,1),('6c898a86ab6641ab8ebee1fa0fe589ba','2016302580263','2019-05-02 14:48:00',NULL,'d1bbb6b155394748a2efc6b8ada3370f',NULL,1),('df3a27f8a1104f099c5782bdfd0f555d','2016302580263','2019-05-02 14:54:40',NULL,NULL,'1002b6960dd949239b44bbcd9ea5de3b',1),('12424cf3cb1245f48c40d1c9dcd5be0f','2016302580263','2019-05-02 15:07:59',NULL,NULL,'1002b6960dd949239b44bbcd9ea5de3b',1),('c7417cb062974f869e825f557b3f42f1','2016302580263','2019-05-02 15:08:00',NULL,NULL,'1002b6960dd949239b44bbcd9ea5de3b',1),('5a717cdd4c714cc197458c5baae6dab8','2016302580263','2019-05-02 16:04:17',NULL,NULL,'1002b6960dd949239b44bbcd9ea5de3b',1);
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

-- Dump completed on 2019-05-02 17:25:14
