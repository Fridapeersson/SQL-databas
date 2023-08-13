/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE `accounts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `account_number` mediumtext NOT NULL,
  `balance` float DEFAULT NULL,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `transactions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `amount` float NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `social_security_number` mediumtext NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone_number` mediumtext NOT NULL,
  `password` varchar(100) NOT NULL,
  `created` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `accounts` (`id`, `user_id`, `account_number`, `balance`, `created`) VALUES
(1, 1, '1234567890', 5000, '2023-05-25 18:31:21');
INSERT INTO `accounts` (`id`, `user_id`, `account_number`, `balance`, `created`) VALUES
(2, 2, '98765443210', 1000, '2023-05-25 18:34:11');
INSERT INTO `accounts` (`id`, `user_id`, `account_number`, `balance`, `created`) VALUES
(3, 3, '64795827341', 4000, '2023-05-25 18:35:09');
INSERT INTO `accounts` (`id`, `user_id`, `account_number`, `balance`, `created`) VALUES
(4, 4, '84759327349', 8000, '2023-05-25 18:38:30'),
(5, 5, '74029384756', 18000, '2023-05-25 18:41:18'),
(6, 6, '38471084326', 10000, '2023-05-25 18:43:17'),
(7, 7, '28374910573', 500, '2023-05-25 18:44:48');

INSERT INTO `transactions` (`id`, `user_id`, `amount`) VALUES
(1, 1, 2000);
INSERT INTO `transactions` (`id`, `user_id`, `amount`) VALUES
(2, 2, 4000);
INSERT INTO `transactions` (`id`, `user_id`, `amount`) VALUES
(3, 3, 2000);
INSERT INTO `transactions` (`id`, `user_id`, `amount`) VALUES
(4, 4, 1000),
(5, 5, 3500),
(6, 6, 1500),
(7, 7, 5000);

INSERT INTO `users` (`id`, `first_name`, `last_name`, `social_security_number`, `email`, `phone_number`, `password`, `created`) VALUES
(1, 'Levis', 'Persson', '202202283905', 'Levis@example.com', '187020090', 'hej123', '2023-05-25 17:48:48');
INSERT INTO `users` (`id`, `first_name`, `last_name`, `social_security_number`, `email`, `phone_number`, `password`, `created`) VALUES
(2, 'Sanna', 'Larsson', '193201293944', 'Sanna@example.com', '938475509', '123lsn', '2023-05-25 17:49:02');
INSERT INTO `users` (`id`, `first_name`, `last_name`, `social_security_number`, `email`, `phone_number`, `password`, `created`) VALUES
(3, 'Sebastian', 'Andersson', '2200109278832', 'Sebastian@example.com', '187020090', 'password123', '2023-05-25 18:31:21');
INSERT INTO `users` (`id`, `first_name`, `last_name`, `social_security_number`, `email`, `phone_number`, `password`, `created`) VALUES
(4, 'Amanda', 'Karlsson', '199908233465', 'Amanda@example.com', '2738495746', 'losenord10', '2023-05-25 18:38:30'),
(5, 'Sven', 'Svensson', '196704214419', 'Sven@example.com', '1704758392', 'blommor23', '2023-05-25 18:41:18'),
(6, 'Pelle', 'Galli', '195003019900', 'Pelle@example.com', '1702738401', 'hund674', '2023-05-25 18:43:16'),
(7, 'Sofia', 'Andersson', '199505258722', 'Sofia@example.com', '1708374612', '643soffan', '2023-05-25 18:44:48');


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;