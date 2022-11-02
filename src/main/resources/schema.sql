CREATE TABLE `point_wallet` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `amount` bigint NOT NULL COMMENT '보유 금액',
    `user_id` varchar(20) NOT NULL COMMENT '유저 ID',
    PRIMARY KEY (`id`)
) COMMENT '포인트지갑';

CREATE TABLE `point_reservation` (
     `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
     `amount` bigint NOT NULL COMMENT '적립금액',
     `available_days` int NOT NULL COMMENT '유효기간',
     `earned_date` date NOT NULL COMMENT '적립일자',
     `is_executed` tinyint NOT NULL COMMENT '적용여부',
     `point_wallet_id` bigint NOT NULL COMMENT '포인트지갑 ID',
     PRIMARY KEY (`id`)
) COMMENT '포인트 예약';

CREATE TABLE `point`(
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `amount` bigint NOT NULL COMMENT '적립금액',
    `earned_date` date NOT NULL COMMENT '적립일자',
    `expire_date` date NOT NULL COMMENT '만료일자',
    `is_used` tinyint NOT NULL COMMENT '사용유무',
    `is_expired` tinyint NOT NULL COMMENT '만료여부',
    `point_wallet_id` bigint NOT NULL COMMENT '포인트지갑 ID',
    PRIMARY KEY (`id`)
) COMMENT '포인트 적립';


CREATE TABLE `message` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` varchar(20) NOT NULL COMMENT '유저 ID',
    `title` varchar(200) NOT NULL COMMENT '제목',
    `content` text NOT NULL COMMENT '내용',
    PRIMARY KEY (`id`)
) COMMENT '메세지';