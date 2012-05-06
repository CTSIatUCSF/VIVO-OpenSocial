
--
-- Table structure for table `shindig_activity`
--

CREATE TABLE IF NOT EXISTS `shindig_activity` (
  `activityId` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) default NULL,
  `appId` int(11) default NULL,
  `createdDT` datetime default NULL,
  `activity` text,
  PRIMARY KEY  (`activityId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `shindig_appdata`
--

CREATE TABLE IF NOT EXISTS `shindig_appdata` (
  `userId` int(11) NOT NULL,
  `appId` int(11) NOT NULL,
  `keyname` varchar(255) NOT NULL,
  `value` varchar(4000) default NULL,
  `createdDT` datetime default NULL,
  `updatedDT` datetime default NULL,
  KEY `userId` (`userId`,`appId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `shindig_apps`
--

CREATE TABLE IF NOT EXISTS `shindig_apps` (
  `appid` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `PersonFilterID` int(11) default NULL,
  `enabled` tinyint(1) NOT NULL default '1',
  `channels` varchar(255) default NULL,
  PRIMARY KEY  (`appid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `shindig_app_registry`
--

CREATE TABLE IF NOT EXISTS `shindig_app_registry` (
  `appid` int(11) NOT NULL,
  `personId` int(11) NOT NULL,
  `createdDT` datetime NOT NULL,
  PRIMARY KEY  (`appid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `shindig_app_views`
--

CREATE TABLE IF NOT EXISTS `shindig_app_views` (
  `appid` int(11) NOT NULL,
  `viewer_req` char(1) default NULL,
  `owner_req` char(1) default NULL,
  `page` varchar(50) default NULL,
  `view` varchar(50) default NULL,
  `closed_width` int(11) default NULL,
  `open_width` int(11) default NULL,
  `start_closed` tinyint(1) default NULL,
  `chromeId` varchar(50) default NULL,
  `display_order` int(11) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `shindig_messages`
--

CREATE TABLE IF NOT EXISTS `shindig_messages` (
  `msgId` varchar(255) NOT NULL,
  `senderId` int(11) default NULL,
  `recipientId` int(11) default NULL,
  `coll` varchar(255) default NULL,
  `title` varchar(255) default NULL,
  `body` varchar(4000) default NULL,
  `createdDT` datetime default NULL,
  PRIMARY KEY  (`msgId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------


DELIMITER // 
CREATE PROCEDURE shindig_registerAppPerson (uid INT, aid INT, v BOOL)
BEGIN
	IF (v)
	THEN
		INSERT INTO shindig_app_registry (appId, personId, createdDT) values (aid, uid, now());
	ELSE 
		DELETE FROM shindig_app_registry where appId = aid AND personId = uid;
	END IF;	
END // 
DELIMITER ; 

DELIMITER // 
CREATE PROCEDURE shindig_upsertAppData(uid INT,aid INT, kn varchar(255),v varchar(4000))
BEGIN
	DECLARE cnt int;
	SELECT count(*) FROM shindig_appdata WHERE userId = uid AND appId = aid and keyname = kn INTO cnt; 
	IF (cnt > 0)
	THEN
		UPDATE shindig_appdata set `value` = v, updatedDT = NOW() WHERE userId = uid AND appId = aid and keyname = kn;
	ELSE
		INSERT INTO shindig_appdata (userId, appId, keyname, `value`) values (uid, aid, kn, v);
	END IF;
		-- if keyname is VISIBLE, do more
	IF (kn = 'VISIBLE' AND v = 'Y') 
	THEN
		CALL shindig_registerAppPerson(uid, aid, 1);
	ELSEIF (kn = 'VISIBLE' )
	THEN
		CALL shindig_registerAppPerson(uid, aid, 0);
	END IF;
END // 
DELIMITER ;					

DELIMITER // 
CREATE PROCEDURE shindig_deleteAppData(uid INT,aid INT, kn varchar(255))
BEGIN
	DELETE FROM shindig_appdata WHERE userId = uid AND appId = aid and keyname = kn;
		-- if keyname is VISIBLE, do more
	IF (kn = 'VISIBLE' ) 
	THEN
		CALL shindig_registerAppPerson(uid, aid, 0);
	END IF;
END // 
DELIMITER ;

/********** Add some gadgets to play with ********************/

--delete from shindig_apps;
insert shindig_apps values (100, 'Google Search', 'http://dev-profiles.ucsf.edu/apps/GoogleSearch.xml', NULL, 1, NULL);
insert shindig_apps values (101, 'Featured Presentations', 'http://dev-profiles.ucsf.edu/apps/SlideShare.xml', NULL, 1, NULL);
insert shindig_apps values (102, 'Faculty Mentor', 'http://dev-profiles.ucsf.edu/apps/Mentor.xml', NULL, 1, NULL);
insert shindig_apps values (103, 'Websites', 'http://dev-profiles.ucsf.edu/apps/Links.xml', NULL, 1, NULL);
insert shindig_apps values (104, 'Profile List', 'http://dev-profiles.ucsf.edu/apps/ProfileListTool.xml', NULL, 1, 'JSONPersonIds');
insert shindig_apps values (105, 'Publication Export', 'http://dev-profiles.ucsf.edu/apps/PubExportTool.xml', NULL, 1, 'JSONPubMedIds');

insert shindig_app_views values (100, NULL, NULL, 'Search.aspx', NULL, 600, 600, 1, 'gadgets-search', NULL);
insert shindig_app_views values (101, NULL, 'R', '/display', 'profile', 291, 590, 1, 'gadgets-view', 3);
insert shindig_app_views values (101, NULL, NULL, '/editForm', 'home', 700,700, 1, 'gadgets-edit', NULL);
insert shindig_app_views values (102, NULL, 'R', '/display', 'profile', 291, 590, 1, 'gadgets-view', 2);
insert shindig_app_views values (102, NULL, NULL, '/editForm', 'home', 700, 700, 1, 'gadgets-edit', NULL);
insert shindig_app_views values (103, NULL, NULL, '/editForm', 'home', 700, 700, 1, 'gadgets-edit', NULL);
insert shindig_app_views values (103, NULL, 'R', '/display', 'profile', 291, 590, 0, 'gadgets-view', 1);
insert shindig_app_views values (104, 'U', NULL, 'Search.aspx', 'small', 160, 160, 0, 'gadgets-tools', NULL);
insert shindig_app_views values (104, 'U', NULL, 'GadgetDetails.aspx', 'canvas', 700, 700, 0, 'gadgets-detail', NULL);
insert shindig_app_views values (104, 'U', NULL, 'SimilarPeople.aspx', 'small', 160, 160, 0, 'gadgets-tools', NULL);
insert shindig_app_views values (104, 'U', NULL, '/display', 'small', 160, 160, 0, 'gadgets-tools', NULL);
insert shindig_app_views values (104, 'U', NULL, 'CoAuthors.aspx', 'small', 160, 160, 0, 'gadgets-tools', NULL);
insert shindig_app_views values (105, 'U', NULL, '/display', 'small', 160, 160, 0, 'gadgets-tools', NULL);
insert shindig_app_views values (105, 'U', NULL, 'GadgetDetails.aspx', 'canvas', 700, 700, 0, 'gadgets-detail', NULL);



