CREATE TABLE IF NOT EXISTS `log_archive` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `log_id` bigint(20) NOT NULL,
    `dateTime` datetime NOT NULL,
    `provider_no` varchar(10) DEFAULT NULL,
    `action` varchar(64) DEFAULT NULL,
    `content` varchar(3000) DEFAULT NULL,
    `contentId` varchar(80) DEFAULT NULL,
    `ip` varchar(64) DEFAULT NULL,
    `demographic_no` int(10) DEFAULT NULL,
    `data` text,
    `securityId` int(11) DEFAULT NULL,
    `status` varchar(32) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=Archive DEFAULT CHARSET=utf8;