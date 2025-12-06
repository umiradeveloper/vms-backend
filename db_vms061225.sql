-- --------------------------------------------------------
-- Host:                         localhost
-- Server version:               8.0.44 - MySQL Community Server - GPL
-- Server OS:                    Linux
-- HeidiSQL Version:             12.13.0.7147
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for vms
CREATE DATABASE IF NOT EXISTS `vms` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `vms`;

-- Dumping structure for table vms.apps
CREATE TABLE IF NOT EXISTS `apps` (
  `id_apps` varchar(100) NOT NULL,
  `code_apps` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `apps_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_apps`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.apps: ~0 rows (approximately)
INSERT INTO `apps` (`id_apps`, `code_apps`, `apps_name`) VALUES
	('73fd69aa-2f3d-4ded-babd-022adc5a2f99', '00', 'VMS List');

-- Dumping structure for table vms.apps_access
CREATE TABLE IF NOT EXISTS `apps_access` (
  `id_apps_access` varchar(100) DEFAULT NULL,
  `code_apps` varchar(100) DEFAULT NULL,
  `id_branch` varchar(100) DEFAULT NULL,
  `id_user` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.apps_access: ~0 rows (approximately)

-- Dumping structure for table vms.branch
CREATE TABLE IF NOT EXISTS `branch` (
  `id_branch` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `nama_branch` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `kode_branch` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_branch`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.branch: ~4 rows (approximately)
INSERT INTO `branch` (`id_branch`, `nama_branch`, `kode_branch`) VALUES
	('0db655e6-c0f5-4719-9edf-5387cbcae343', 'Jogjakarta', '04'),
	('58d86ea4-bb68-46ff-8310-e026332599fa', 'Bali', '01'),
	('9087692d-2860-455a-87b4-1d2addd5ce0e', 'Surabaya', '02'),
	('f65caa49-5886-444e-a723-6a645e4b1789', 'Jakarta', '03');

-- Dumping structure for table vms.cc_biaya_kontruksi
CREATE TABLE IF NOT EXISTS `cc_biaya_kontruksi` (
  `id_biaya_kontruksi` varchar(100) DEFAULT NULL,
  `nama_vendor` varchar(100) DEFAULT NULL,
  `volume_bk` decimal(10,0) DEFAULT NULL,
  `harga_total` bigint DEFAULT NULL,
  `nama_penerima` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.cc_biaya_kontruksi: ~0 rows (approximately)

-- Dumping structure for table vms.cc_kategori
CREATE TABLE IF NOT EXISTS `cc_kategori` (
  `id_kategori` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `nama_kategori` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `kode_kategori` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id_kategori`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.cc_kategori: ~3 rows (approximately)
INSERT INTO `cc_kategori` (`id_kategori`, `nama_kategori`, `kode_kategori`) VALUES
	('96c40cf9-ba12-42a1-abc3-c1f62d102196', 'tes4', 'tes4'),
	('c99bc64c-dace-4d76-95a3-56fb7c08af9f', 'tes2', 'tes2'),
	('ed991358-3fdd-414d-9839-86bd07e9fde9', 'tes5', 'tes5');

-- Dumping structure for table vms.cc_kategori_rap
CREATE TABLE IF NOT EXISTS `cc_kategori_rap` (
  `id_kategori_rap` varchar(100) NOT NULL,
  `nama_kategori` varchar(100) DEFAULT NULL,
  `kode_kategori` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_kategori_rap`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.cc_kategori_rap: ~0 rows (approximately)

-- Dumping structure for table vms.cc_proyek
CREATE TABLE IF NOT EXISTS `cc_proyek` (
  `id_proyek` varchar(100) NOT NULL,
  `nama_proyek` varchar(100) DEFAULT NULL,
  `kode_proyek` varchar(100) DEFAULT NULL,
  `deskripsi_proyek` text,
  `biaya_rap` int DEFAULT NULL,
  `biaya_rab` int DEFAULT NULL,
  `tanggal_awal_kontrak` date DEFAULT NULL,
  `tanggal_akhir_kontrak` date DEFAULT NULL,
  PRIMARY KEY (`id_proyek`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.cc_proyek: ~2 rows (approximately)
INSERT INTO `cc_proyek` (`id_proyek`, `nama_proyek`, `kode_proyek`, `deskripsi_proyek`, `biaya_rap`, `biaya_rab`, `tanggal_awal_kontrak`, `tanggal_akhir_kontrak`) VALUES
	('463af275-4b6f-4480-b603-05779cb78e34', 'testerious', '0011', 'tester', 1000000000, 1101010, '2025-11-26', '2025-11-26'),
	('64c4674f-b315-4772-8b25-93fe5f0906f9', 'test', '001', 'test', 1000000000, 1000000000, '2025-11-26', '2025-11-26');

-- Dumping structure for table vms.cc_rapa
CREATE TABLE IF NOT EXISTS `cc_rapa` (
  `id_rapa` varchar(100) NOT NULL,
  `id_proyek` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `kategori` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `kode_rap` varchar(100) DEFAULT NULL,
  `group` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `item_pekerjaan` varchar(255) DEFAULT NULL,
  `spesifikasi` text,
  `satuan` varchar(100) DEFAULT NULL,
  `volume` decimal(10,0) DEFAULT NULL,
  `harga_satuan` int DEFAULT NULL,
  `harga_total` bigint DEFAULT NULL,
  PRIMARY KEY (`id_rapa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.cc_rapa: ~0 rows (approximately)

-- Dumping structure for table vms.cc_satuan
CREATE TABLE IF NOT EXISTS `cc_satuan` (
  `id_satuan` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `nama_satuan` varchar(100) DEFAULT NULL,
  `kode_satuan` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_satuan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.cc_satuan: ~2 rows (approximately)
INSERT INTO `cc_satuan` (`id_satuan`, `nama_satuan`, `kode_satuan`) VALUES
	('c6e010f9-293a-4930-9e38-ed12c960c91e', 'tes', 'tes');

-- Dumping structure for table vms.menu
CREATE TABLE IF NOT EXISTS `menu` (
  `id_menu` varchar(100) NOT NULL,
  `code_apps` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `code_menu` varchar(100) DEFAULT NULL,
  `nama_menu` varchar(100) DEFAULT NULL,
  `menu_title` varchar(100) DEFAULT NULL,
  `icon_menu` varchar(100) DEFAULT NULL,
  `type_menu` varchar(100) DEFAULT NULL,
  `active_menu` varchar(100) DEFAULT NULL,
  `selected_menu` varchar(100) DEFAULT NULL,
  `dircange_menu` varchar(100) DEFAULT NULL,
  `path_menu` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_menu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.menu: ~35 rows (approximately)
INSERT INTO `menu` (`id_menu`, `code_apps`, `code_menu`, `nama_menu`, `menu_title`, `icon_menu`, `type_menu`, `active_menu`, `selected_menu`, `dircange_menu`, `path_menu`) VALUES
	('038765e2-bb8d-4920-9643-f63c49a2723a', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|01|01', 'Form Pendaftaran Vendor', NULL, 'ti-brefcase', 'link', 'false', 'false', 'false', '/apps/FormVms'),
	('0cbbcb64-19c1-4154-bbb4-faa78bc35a66', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '00', NULL, 'Dashboard', NULL, NULL, NULL, NULL, NULL, NULL),
	('0e4dd2c1-235a-421f-97e1-d942898ee526', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|01|04', 'List Vendor', NULL, 'ti-brefcase', 'link', 'false', 'false', 'false', '/apps/ListVms'),
	('1740f12c-f454-4db2-97ef-5d2245205d3b', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|03|03', 'Monitoring Quality', NULL, 'ti-user', 'link', NULL, 'false', 'false', ''),
	('18414845-44cf-424f-b647-8ed12f710fc9', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|03|08', 'Approval Environment', NULL, 'ti-user', 'link', NULL, 'false', 'false', ''),
	('1eb1f34d-a271-4d30-a851-5bd1531a3e7e', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01', NULL, 'Apps', NULL, NULL, NULL, NULL, NULL, NULL),
	('20fdeb3b-5fbe-4a76-9775-bd0d9811df37', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|01|06', 'List Vendor Master', NULL, 'ti-list', 'link', NULL, 'false', 'false', '/apps/ListVendorMaster'),
	('295b6be7-96dd-4f20-afac-1ba6af91bb88', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '00|01', 'Dashboard', NULL, 'ti-dashboard', 'link', 'true', 'true', 'false', '/apps/DashboardVms'),
	('3209b1fc-1ea9-42d8-9aa5-d7b96200b4fb', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|01|03', 'List Pengajuan Vendor', NULL, 'ti-brefcase', 'link', 'false', 'false', 'false', '/apps/ListPengajuanVms'),
	('348f9714-33d7-49f5-997b-3bdf3e5d35d4', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '02|02', 'User Management Vendor', NULL, 'ti-user', 'link', NULL, 'false', 'false', '/apps/UserManagementVendor'),
	('34a0a0f2-de70-4cd4-b1a1-147ee8d8ceb0', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|03|02', 'Approval Quality', NULL, 'ti-user', 'link', NULL, 'false', 'false', ''),
	('4e054ecf-7ca2-4c23-bae5-1788a04b14e5', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|03|05', 'Monitoring Safety', NULL, 'ti-user', 'link', NULL, 'false', 'false', ''),
	('5a40b9c9-ad4d-4ee1-857b-a42fc230951a', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|03|01', 'Quality', NULL, 'ti-user', 'link', NULL, 'false', 'false', ''),
	('5d87e913-2bf3-46d7-8c99-0662433e4510', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|02|06', 'Daftar Kategori', NULL, 'ti-user', 'link', NULL, 'false', 'false', '/apps/CostControl/Kategori/DaftarKategori'),
	('68ddcf07-3df3-4e80-a57c-39bbc2790d89', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|03|07', 'Approval Health', NULL, 'ti-user', 'link', NULL, 'false', 'false', ''),
	('6d334e40-69c6-45e0-9cb8-6c6d1db9a13c', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|01|02', 'Draft Pengajuan Vendor', NULL, 'ti-brefcase', 'link', 'false', 'false', 'false', '/apps/DraftPengajuanVms'),
	('715a8685-b9bc-4dc6-88c4-7465b1ab1ec5', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|03|04', 'Approval Safety', NULL, 'ti-user', 'link', NULL, 'false', 'false', ''),
	('7762cc8a-e1d6-4f40-8bb4-c23f00fbdc68', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|03|04', 'Safety', NULL, 'ti-user', 'link', NULL, 'false', 'false', ''),
	('7980a1ac-0611-47f8-9a02-0c4980daa5bb', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|03|07', 'Monitoring Health', NULL, 'ti-user', 'link', NULL, 'false', 'false', ''),
	('8d20da00-21bb-4496-86d1-c106c6ac93b9', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|02', 'Cost Control', NULL, 'ti-briefcase', 'sub', NULL, 'false', 'false', ''),
	('940ae2cc-90e0-4ee4-8efc-af47a4b4cf31', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|01|05', 'Monitoring Pengajuan', NULL, 'ti-people', 'link', NULL, 'false', 'false', '/apps/MonitoringPengajuan'),
	('ac5c5f73-b6d7-4d04-9fa8-46723cac5a87', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|03|06', 'Health', NULL, 'ti-user', 'link', NULL, 'false', 'false', ''),
	('ac7da452-4efc-4601-9c4a-03f631321662', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|02|03', 'RAPA', NULL, 'ti-user', 'link', NULL, 'false', 'false', '/apps/CostControl/Rapa/DaftarRapa'),
	('af63798d-d01a-4a9d-b2da-f42339e2e77f', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '02', NULL, 'Settings', NULL, NULL, NULL, NULL, NULL, NULL),
	('b001cba6-f94e-4e75-b56b-413ce4f71a1c', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|01|08', 'List Monitoring Update Vendor', NULL, 'ti-list', 'link', NULL, 'false', 'false', '/apps/ListMonitoringUpdateVendor'),
	('b9ea3ebf-189d-4b71-b118-1b2fa0974526', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|03|08', 'Environment', NULL, 'ti-user', 'link', NULL, 'false', 'false', ''),
	('bca09870-5aca-4a67-9941-6d475ac68903', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|01', 'SIM Vendor Umira', NULL, 'ti-briefcase', 'sub', 'false', 'false', 'false', ''),
	('c2d0448c-ea53-497c-af86-f4db1eb3c6b6', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '02|01', 'User Management', NULL, 'ti-user', 'link', 'false', 'false', 'false', '/apps/UserManagement'),
	('c829e7a9-fc9c-47cf-b0a3-8cace4795999', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|02|05', 'Daftar Satuan', NULL, 'ti-user', 'link', NULL, 'false', 'false', '/apps/CostControl/Satuan/DaftarSatuan'),
	('ccb5b563-0c9c-406b-b9b2-3b1d0a191927', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|01|06', 'List Vendor Master', NULL, 'ti-list', 'link', NULL, 'false', 'false', '/apps/ListVendorMaster'),
	('de7ee674-19d0-4515-a5b3-ed6c51013939', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|03|09', 'Monitoring Environment', NULL, 'ti-user', 'link', NULL, 'false', 'false', ''),
	('e1761d2a-534e-4899-b85d-7669a62d2e10', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|03', 'QHSE', NULL, 'ti-user', 'sub', NULL, 'false', 'false', ''),
	('e41bb5af-5132-45c8-9b43-25880f56ac21', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|02|04', 'Buat Satuan', NULL, 'ti-user', 'link', NULL, 'false', 'false', '/apps/CostControl/Satuan/CreateSatuan'),
	('fb907101-0ae5-4590-8410-b38f801fa3b1', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|02|02', 'Daftar Proyek', NULL, 'ti-user', 'link', NULL, 'false', 'false', '/apps/CostControl/Proyek/DaftarProyek'),
	('fb93383b-9de9-4b26-baba-fc0470898ba2', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|02|01', 'Proyek', NULL, 'ti-user', 'link', NULL, 'false', 'false', '/apps/CostControl/Proyek/CreateProyek'),
	('fc19e1b4-8038-47e9-93e4-24b96da1c6b7', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '01|01|07', 'List Pengajuan Update Vendor', NULL, 'ti-list', 'link', NULL, 'false', 'false', '/apps/ListPengajuanUpdateVendor');

-- Dumping structure for table vms.menu_access
CREATE TABLE IF NOT EXISTS `menu_access` (
  `id_menu_access` varchar(100) NOT NULL,
  `code_apps` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `code_menu` varchar(100) DEFAULT NULL,
  `id_role` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id_menu_access`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.menu_access: ~24 rows (approximately)
INSERT INTO `menu_access` (`id_menu_access`, `code_apps`, `code_menu`, `id_role`) VALUES
	('00bebeb5-4b57-48e0-801a-084f4f0ce301', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '3209b1fc-1ea9-42d8-9aa5-d7b96200b4fb', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('07451e30-25a2-42c3-8f21-f5611e77ff9b', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '0cbbcb64-19c1-4154-bbb4-faa78bc35a66', '13754b4f-2614-47aa-9516-4d0161793d4e'),
	('25150341-555e-451f-8045-c103e785bae7', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '20fdeb3b-5fbe-4a76-9775-bd0d9811df37', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('29895ce8-1a7d-4157-9d46-800c87bc4bc4', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '038765e2-bb8d-4920-9643-f63c49a2723a', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('354b9708-ee0c-4c0a-b8c0-e36771b60980', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '2a70f371-7dde-4ed4-b4c7-b9d7a899ae3b', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('4509e145-126f-41f4-987b-e400bb8793bc', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '4be23170-c242-4693-abb2-82114d551d34', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('4b682772-4a2d-4f6c-b40a-5db88d728212', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '348f9714-33d7-49f5-997b-3bdf3e5d35d4', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('4ef6d747-b7b0-4006-8f46-b019b2380708', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '0e4dd2c1-235a-421f-97e1-d942898ee526', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('5262b215-9110-4f86-b9c0-27ee012c06eb', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', 'ad20d382-a04d-498d-81ba-55f55476222c', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('64c57b1d-5d1f-4d6d-9bab-e48c8e767c44', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '295b6be7-96dd-4f20-afac-1ba6af91bb88', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('673e773a-44f0-4f79-8e4f-3f567be1f43d', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', 'acf9856b-8405-4fcf-8126-6eb7e9754c9e', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('745b4c41-fc37-4285-83fc-7cf0ce42899a', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', 'c829e7a9-fc9c-47cf-b0a3-8cace4795999', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('79c0c8c3-6f85-4a75-afc0-b08dbfd7a6e5', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '5d87e913-2bf3-46d7-8c99-0662433e4510', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('88fe90f3-18ab-4beb-8885-74d04010f72c', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', 'b001cba6-f94e-4e75-b56b-413ce4f71a1c', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('941f9e70-69a6-4ebd-972b-9536a141fac8', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', 'c2d0448c-ea53-497c-af86-f4db1eb3c6b6', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('9bfc0af4-e606-4e12-9df0-1e3a5be55e26', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '940ae2cc-90e0-4ee4-8efc-af47a4b4cf31', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('a68a2e25-a42a-43a0-8922-9f299d77f818', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '339781ce-2f68-4663-9e87-92433f5b127b', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('a8035b79-d2e4-4bb6-95ee-69923a1dad95', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '0cbbcb64-19c1-4154-bbb4-faa78bc35a66', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('a9b7d528-44a6-440b-ad61-2b7276866c5c', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', 'ac7da452-4efc-4601-9c4a-03f631321662', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('b27f1f28-dd10-4262-b03b-15f89dcbce89', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', 'fc19e1b4-8038-47e9-93e4-24b96da1c6b7', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('c2145f75-de5f-4556-98eb-20b8b57ecf90', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', 'e9b37a81-70a8-46c7-ba63-5cc58f9036d7', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('c2c6030c-9421-4d3a-bea7-db9513f5245a', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', 'af63798d-d01a-4a9d-b2da-f42339e2e77f', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('c944ced1-b766-4019-a4ba-8000f2da944c', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', 'fb907101-0ae5-4590-8410-b38f801fa3b1', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('c9c74b7f-75f5-4782-ae2f-f89b93981af3', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', 'bca09870-5aca-4a67-9941-6d475ac68903', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('cbfb2dc2-d175-45e4-b756-c5277d5ecdd8', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '237d641a-2184-4124-af88-694f8ce6c59d', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('cef644cb-4a3c-4e6e-84e9-f34023be2505', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '6d334e40-69c6-45e0-9cb8-6c6d1db9a13c', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('dcbb0208-771f-4c99-b01e-516ff6cae32d', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', 'e41bb5af-5132-45c8-9b43-25880f56ac21', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('e56d2abc-42ad-4fbb-9841-7a91e6d4e0d8', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '534e943c-b281-4c7b-b3e4-128b75c7fe9e', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('e76db6e8-6b26-4f7d-abf3-38ae0f6db17f', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', 'a28cfe73-d825-43de-b9d8-43eb3c3757d6', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('f324e064-68d3-4291-94bc-9448185b12e9', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', 'fb93383b-9de9-4b26-baba-fc0470898ba2', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('f3b8c061-bee8-4a6e-b754-3a6f69b1f438', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '8d20da00-21bb-4496-86d1-c106c6ac93b9', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387'),
	('fe1b6702-f743-455a-b90c-b119e18e6277', '73fd69aa-2f3d-4ded-babd-022adc5a2f99', '1eb1f34d-a271-4d30-a851-5bd1531a3e7e', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387');

-- Dumping structure for table vms.menu_access_mobile
CREATE TABLE IF NOT EXISTS `menu_access_mobile` (
  `id_menu_access_mobile` varchar(100) NOT NULL,
  `id_menu_mobile` varchar(100) DEFAULT NULL,
  `id_role` varchar(100) DEFAULT NULL,
  `id_apps` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_menu_access_mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.menu_access_mobile: ~6 rows (approximately)
INSERT INTO `menu_access_mobile` (`id_menu_access_mobile`, `id_menu_mobile`, `id_role`, `id_apps`) VALUES
	('0d670ec1-8e39-4c2f-80a9-62c978b7fa61', '9a876398-9c95-4fec-855e-92583dfec650', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387', '73fd69aa-2f3d-4ded-babd-022adc5a2f99'),
	('3d33bce3-bad0-47c8-a079-c7e1fc9cfafd', 'd1d879fe-12cd-409b-b977-57fe0b6b1544', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387', '73fd69aa-2f3d-4ded-babd-022adc5a2f99'),
	('43744d8f-9aa0-48e5-8041-a49c3a71d88f', 'd1d879fe-12cd-409b-b977-57fe0b6b1544', '13754b4f-2614-47aa-9516-4d0161793d4e', '73fd69aa-2f3d-4ded-babd-022adc5a2f99'),
	('91fc21fe-59bf-4090-8301-327ed1a6a320', '8a42e6bc-73e5-4e34-a1bb-462d9a3e8361', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387', '73fd69aa-2f3d-4ded-babd-022adc5a2f99'),
	('af2c8793-4a7a-456d-99d2-473fcf77f63f', '88617dc9-6d90-43b2-b0fe-73b32d09cb45', '13754b4f-2614-47aa-9516-4d0161793d4e', '73fd69aa-2f3d-4ded-babd-022adc5a2f99'),
	('fb267533-7cf1-4b9c-b68d-5bb496e00e70', '88617dc9-6d90-43b2-b0fe-73b32d09cb45', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387', '73fd69aa-2f3d-4ded-babd-022adc5a2f99');

-- Dumping structure for table vms.menu_mobile
CREATE TABLE IF NOT EXISTS `menu_mobile` (
  `id_menu_mobile` varchar(100) NOT NULL,
  `kode_menu` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `menu_name` varchar(100) DEFAULT NULL,
  `menu_path` varchar(100) DEFAULT NULL,
  `menu_icon` varchar(100) DEFAULT NULL,
  `id_apps` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_menu_mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.menu_mobile: ~4 rows (approximately)
INSERT INTO `menu_mobile` (`id_menu_mobile`, `kode_menu`, `menu_name`, `menu_path`, `menu_icon`, `id_apps`) VALUES
	('88617dc9-6d90-43b2-b0fe-73b32d09cb45', '03', 'Monitoring Pengajuan', 'Monitoring Pengajuan', 'home', '73fd69aa-2f3d-4ded-babd-022adc5a2f99'),
	('8a42e6bc-73e5-4e34-a1bb-462d9a3e8361', '04', 'List Vendor', 'List Vendor', 'home', '73fd69aa-2f3d-4ded-babd-022adc5a2f99'),
	('9a876398-9c95-4fec-855e-92583dfec650', '02', 'Approval Pengajuan', 'Approval', 'home', '73fd69aa-2f3d-4ded-babd-022adc5a2f99'),
	('d1d879fe-12cd-409b-b977-57fe0b6b1544', '01', 'Home', 'HomeVendor', 'home', '73fd69aa-2f3d-4ded-babd-022adc5a2f99');

-- Dumping structure for table vms.project_done
CREATE TABLE IF NOT EXISTS `project_done` (
  `id_project_done` varchar(100) NOT NULL,
  `nama_project` varchar(100) DEFAULT NULL,
  `deskripsi_project` text,
  `url_gambar` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  PRIMARY KEY (`id_project_done`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.project_done: ~9 rows (approximately)
INSERT INTO `project_done` (`id_project_done`, `nama_project`, `deskripsi_project`, `url_gambar`) VALUES
	('2378ba00-396e-4d14-96fc-7da86aec1278', 'Satori', 'Satori abccd', 'uploads/projectdone/c7f4cf0d-a884-4851-8296-fb4558d05bc3.jpeg,uploads/projectdone/28f8035b-d042-484f-84ef-cf35617e1678.jpg,uploads/projectdone/bd7ebf47-c51c-4197-852d-393a72574b5e.png'),
	('29da449a-421f-4b50-bd6f-a6362e17083c', 'Satori', 'Satori abccd', 'uploads/projectdone/312f61a4-d888-4d3b-a445-99183bbfca4f.jpeg,uploads/projectdone/eb5d5a5b-b790-4f8f-938d-8314c5aae8e2.jpg,uploads/projectdone/82b54f3a-ec70-4c2c-a702-1abc5aeddd74.png'),
	('43653a85-c9c0-462b-ac0b-772cb8184307', 'Satori', 'Satori abccd', 'uploads/projectdone/ef7958df-bc44-4826-93ca-22d358a3b7ef.jpeg,uploads/projectdone/64287101-96fb-46cd-9916-4fa091d56479.jpg,uploads/projectdone/1852f2de-22e6-4454-afe6-4725453b5fe6.png'),
	('6bd3f527-bdc5-483a-a545-26f1d8de50e9', 'Satori', 'Satori abccd', 'uploads/projectdone/40b6f73c-a413-4d66-b465-073d7b9fc0bb.jpeg,uploads/projectdone/5caca17f-5adc-4d24-b69c-373bf9c92a40.jpg,uploads/projectdone/74b41d14-78dc-4e2f-940d-b7d49660b10a.png'),
	('88a4f0be-7531-41df-9de3-58ccdc9ca816', 'Satori', 'Satori abccd', 'uploads/projectdone/2ef32abe-f9f2-4239-b4eb-f7a9b3a95f98.jpeg,uploads/projectdone/392711a1-c667-48bb-b028-a8a3d22b83c3.jpg,uploads/projectdone/13093ee8-f7f4-43f3-9bf7-fa20fcc36afc.png'),
	('a97cfcb5-7522-442f-96d1-75e5cc12c669', 'Satori', 'Satori abccd', 'uploads/projectdone/391ecbd5-3852-4e72-b4f8-6b8eba062555.jpeg,uploads/projectdone/ec06fc6b-346f-4b8d-9870-3ffc2cd5cfdd.jpg,uploads/projectdone/b01ceba6-268c-40b6-88b1-84c1a5eb573f.png'),
	('c54c6304-36a9-4233-9d69-23f1429e5759', 'Satori', 'Satori abccd', 'uploads/projectdone/54445a78-280b-4c53-945f-a1aac5d882dd.jpeg'),
	('cafae173-89c4-4869-a97a-1aaec90d3236', 'Satori', 'Satori abccd', 'uploads/projectdone/e7c376c9-7a2d-4b6e-95fb-8ae6099c7c36.jpeg,uploads/projectdone/af9591ab-d5d5-4058-bc04-b5de0a35b579.jpg,uploads/projectdone/539840e1-562a-4859-a8a3-8a1d342d82f7.png'),
	('ddea3938-4487-4cce-8af9-e19b8efb14ca', 'Satori', 'Satori abccd', 'uploads/projectdone/4b15d78f-74a8-4976-bd2d-73247421f48d.jpeg,uploads/projectdone/ce235ad2-02fb-46db-b278-b9c5ac385ccc.jpg,uploads/projectdone/fa4d6a00-8243-4556-a62b-87fd95723a96.png');

-- Dumping structure for table vms.project_on_going
CREATE TABLE IF NOT EXISTS `project_on_going` (
  `id_project_on_going` varchar(100) NOT NULL,
  `nama_project` varchar(100) DEFAULT NULL,
  `deskripsi_project` text,
  `url_gambar` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  PRIMARY KEY (`id_project_on_going`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.project_on_going: ~0 rows (approximately)
INSERT INTO `project_on_going` (`id_project_on_going`, `nama_project`, `deskripsi_project`, `url_gambar`) VALUES
	('0b24de98-1222-418c-adcc-a5734abf3017', 'Umi Hotel', 'Umi Hotel', 'uploads/projectongoing/30ecace8-c6ba-49b4-b57b-54e0c2210d5c.jpg,uploads/projectongoing/186471d0-9e77-419d-aff5-549825c61ca0.png');

-- Dumping structure for table vms.role
CREATE TABLE IF NOT EXISTS `role` (
  `id_role` varchar(100) NOT NULL,
  `nama_role` varchar(100) DEFAULT NULL,
  `kode_role` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.role: ~6 rows (approximately)
INSERT INTO `role` (`id_role`, `nama_role`, `kode_role`) VALUES
	('13754b4f-2614-47aa-9516-4d0161793d4e', 'Vendor', '01'),
	('5b750e69-9c08-4828-b72e-397d4c7527ab', 'Project Manager', '05'),
	('8cdc8de3-8e82-4c88-977f-bd1c7b91c387', 'Super Admin', '99'),
	('9c3b3ab1-f41b-49d1-bde3-4070910afc16', 'General Manager', '03'),
	('aa6fe3a6-51f7-4f9b-bbb7-c80446e0ede9', 'Staff', '02'),
	('df3f916e-0483-4bda-b04a-77a16d1c87a8', 'Site Comercial Manager', '04');

-- Dumping structure for table vms.users
CREATE TABLE IF NOT EXISTS `users` (
  `id_user` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `id_branch` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `id_role` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `nama` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `nip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `password` text,
  `email` varchar(100) DEFAULT NULL,
  `no_hp` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `isApproval` int DEFAULT '0',
  `approvedBy` varchar(100) DEFAULT NULL,
  `approvedAt` datetime DEFAULT NULL,
  `catatan` text,
  PRIMARY KEY (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.users: ~10 rows (approximately)
INSERT INTO `users` (`id_user`, `id_branch`, `id_role`, `nama`, `nip`, `username`, `password`, `email`, `no_hp`, `isApproval`, `approvedBy`, `approvedAt`, `catatan`) VALUES
	('012bca9b-aa86-4381-a97b-f9e0a6f8996b', '58d86ea4-bb68-46ff-8310-e026332599fa', '13754b4f-2614-47aa-9516-4d0161793d4e', 'PT Perusahaan ABCDEFGH', NULL, NULL, '$2a$10$YYH.lRACzCUnwRLwGWq1wusctStYenCKXyK/cNMyB6ProAEP6krWu', 'hafid@gmail.com', NULL, 1, NULL, '2025-09-02 13:45:35', NULL),
	('156c7a11-6ced-4a5e-9124-2fbda6a66d05', 'f65caa49-5886-444e-a723-6a645e4b1789', '13754b4f-2614-47aa-9516-4d0161793d4e', 'PT Pilar Innovasi Mandiri', NULL, NULL, '$2a$10$UWsQ0C5xPo1/TpjqdO2c4OOYjV2bO4lShh0sykZVYr5EvJWatiCay', 'hafidy@gmail.com', '+628121260880', 2, 'hafidhanafid', '2025-08-30 14:57:40', 'Perusahaan Baru'),
	('5182c939-4a8c-4487-897c-0d22404293ae', '58d86ea4-bb68-46ff-8310-e026332599fa', '13754b4f-2614-47aa-9516-4d0161793d4e', 'PT Umira Sinergi Global', NULL, NULL, '$2a$10$vGf/3LXcql2lcSDxuNK.Teq8poc/MBF2LVSzsKOLAxl6deqgfJwz6', 'email@gmail.com', '2389329839832', 0, NULL, NULL, NULL),
	('638d7eac-110d-46d1-af4a-60509fde2a9c', '58d86ea4-bb68-46ff-8310-e026332599fa', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387', NULL, NULL, 'hafidhanafid', '$2a$10$gWf9DrDLcIaMiglXOjWCWu9YqS/oQb9N5Cu25.oR9ufaCYBbJ.nwq', 'admin@gmail.com', NULL, 1, 'administrator', NULL, NULL),
	('66d8686d-4783-4f9e-b8b5-a0e7f16d682c', 'f65caa49-5886-444e-a723-6a645e4b1789', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387', 'PT Umira Sinergi Global', NULL, NULL, '$2a$10$2TLKfH4CINQ9zZsJZvGvZOi2Eq.2bJIhP7xipVlRwigQOoLMoGw.K', 'hafidhanafidays@gmail.com', '081384456729', 0, NULL, NULL, NULL),
	('70fcb7c9-9101-458f-a0bf-454bdde3dae1', '58d86ea4-bb68-46ff-8310-e026332599fa', '8cdc8de3-8e82-4c88-977f-bd1c7b91c387', 'PT Umira Sinergi Global', NULL, 'Hafid Hanafi', '$2a$10$xGQuhNUJ3eJT3Hdcpmb4WeyYG5MtsYfHv24yUgMDCP18pig5EzQ5a', 'hafidhanafiday@gmail.com', '081384456729', 0, NULL, NULL, NULL),
	('74abdc1b-67a1-445f-81ef-5c6479b3cae0', 'f65caa49-5886-444e-a723-6a645e4b1789', '13754b4f-2614-47aa-9516-4d0161793d4e', 'Pilar Innovasi Mandiri', NULL, NULL, '$2a$10$J19PU70ham0ap6Jkp78zyOm55Pc/4P20wRn1hX1mH7fIJ1ETFwb1.', 'test-r2qbpg9ta@srv1.mail-tester.com', '+62812126088000', 2, 'hafidhanafid', '2025-08-30 15:55:40', 'tester'),
	('844abc05-9c8a-46ce-8ac2-96e2a54abd3f', 'f65caa49-5886-444e-a723-6a645e4b1789', '13754b4f-2614-47aa-9516-4d0161793d4e', 'Perusahaan ABCD', NULL, NULL, '$2a$10$4niLat0izCRY3GBIPdvwDeU73NutHlse3CrQLtPc3i0euHj20Dtl.', 'hafidt@gmail.com', '081384456729', 0, NULL, NULL, NULL),
	('d092899d-ea57-4c0a-bf3a-17c529bd4987', '58d86ea4-bb68-46ff-8310-e026332599fa', '13754b4f-2614-47aa-9516-4d0161793d4e', 'Perusahaan ABCDS', NULL, NULL, '$2a$10$xG0JPStfIVIRXIsoxL3ACOpzr4ey1GmLiteTmAsJoohwrcE3zXSq6', 'hafid77@gmail.com', '081384456729', 0, NULL, NULL, NULL),
	('f2edf55e-d987-46c3-8dc5-e27f6872e0b5', '58d86ea4-bb68-46ff-8310-e026332599fa', '13754b4f-2614-47aa-9516-4d0161793d4e', 'Perusahaan ABCDS', NULL, NULL, '$2a$10$LfHdddlfbnVM0XgRa7jLcOsWhGEfW7sok0QgL0raCcMy456Efyh/e', 'hafid7@gmail.com', '081384456729', 0, NULL, NULL, NULL);

-- Dumping structure for table vms.vms_vendor
CREATE TABLE IF NOT EXISTS `vms_vendor` (
  `id_vendor` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `id_pengajuan` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `id_user` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `nama_perusahaan` varchar(100) DEFAULT NULL,
  `tanggal_pengajuan` datetime DEFAULT NULL,
  `id_kualifikasi_usaha` varchar(100) DEFAULT NULL,
  `klasifikasi_usaha` varchar(100) DEFAULT NULL,
  `alamat_perusahaan` text,
  `kategori` varchar(100) DEFAULT NULL,
  `spesialisasi` varchar(100) DEFAULT NULL,
  `nama_pic` varchar(100) DEFAULT NULL,
  `email_pic` varchar(100) DEFAULT NULL,
  `no_hp_pic` varchar(100) DEFAULT NULL,
  `nama_direktur` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `email_direktur` varchar(100) DEFAULT NULL,
  `no_hp_direktur` varchar(100) DEFAULT NULL,
  `website` varchar(100) DEFAULT NULL,
  `isApproval` int DEFAULT NULL,
  `approvedBy` varchar(100) DEFAULT NULL,
  `approvedAt` datetime DEFAULT NULL,
  `catatan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `no_skt` varchar(100) DEFAULT NULL,
  `tanggal_awal_skt` date DEFAULT NULL,
  `tanggal_akhir_skt` date DEFAULT NULL,
  PRIMARY KEY (`id_vendor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.vms_vendor: ~18 rows (approximately)
INSERT INTO `vms_vendor` (`id_vendor`, `id_pengajuan`, `id_user`, `nama_perusahaan`, `tanggal_pengajuan`, `id_kualifikasi_usaha`, `klasifikasi_usaha`, `alamat_perusahaan`, `kategori`, `spesialisasi`, `nama_pic`, `email_pic`, `no_hp_pic`, `nama_direktur`, `email_direktur`, `no_hp_direktur`, `website`, `isApproval`, `approvedBy`, `approvedAt`, `catatan`, `no_skt`, `tanggal_awal_skt`, `tanggal_akhir_skt`) VALUES
	('15c2a2a7-7aba-43eb-86a1-e08b6291a8f0', 'VMS.Pengajuan.20112025144623', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-20 14:46:23', '8d1eba75-0462-4ceb-a0e2-eea25164a38b', 'Pabrik', 'jl gunung pajeleran sukahati', 'Pondasi', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '081456437299', 'Hafid', 'hafidhanafid@gmail.com', '081522667979', 'www.hafid.com', 0, 'hafidhanafid', '2025-11-21 16:22:24', 'test', NULL, NULL, NULL),
	('18c9c89e-eb76-4a13-996c-5174be5a5ec7', 'VMS.Pengajuan.02122025133709', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'fadhli test', '2025-12-02 13:37:09', '7d87c121-fdc8-4346-aa9a-7f27b65ed80a', 'Toko / Retail', 'tes', 'Arsitektur', 'pasir', 'test', 'test@gmail.com', '09789563', 'test', 'test@gmail.com', '098724312546', 'www.test.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('1eaa333b-e985-49df-8d3e-b04f9609692d', 'VMS.Pengajuan.21112025091425', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-21 09:14:26', '8d1eba75-0462-4ceb-a0e2-eea25164a38b', 'Distributor', 'jl gunung pajeleran sukahati', 'Pondasi', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '081522667979', 'Hafid', 'hafidhanafid@gmail.com', '0814564142516', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('210d57c3-6a45-4d80-83ab-719d51935026', 'VMS.Pengajuan.20112025140134', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-20 14:01:35', '5dfcd29b-8995-4abb-899f-062ec123662c', 'Sub Kontraktor', 'jl gunung pajeleran sukahati', 'Pekerjaan tanah', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '081384456729', 'Hafid', 'hafidhanafid@gmail.com', '081384456729', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('27f5e798-ef25-4660-9d8f-bcf6bce4454c', 'VMS.Pengajuan.20112025145328', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-20 14:53:28', '8d1eba75-0462-4ceb-a0e2-eea25164a38b', 'Pabrik', 'jl gunung pajeleran sukahati', 'Pekerjaan tanah', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '081456437299', 'Hafid', 'hafidhanafid@gmail.com', '0814564142516', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('2bc567f4-a4fe-4158-b7ec-190ea8fb24cb', 'VMS.Pengajuan.30082025132519', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'PT Pilar innovasi mandiri', '2025-08-30 13:25:19', '8d1eba75-0462-4ceb-a0e2-eea25164a38b', 'Pabrik', 'jl gunung pajeleran sukahati BEckup, test', 'Pekerjaan tanah', 'Kontraktor, testter, akuisisi', 'Hafid', 'hafid@gmail.com', '0723989731281', 'Hafid', 'hafid@gmail.com', '983208983208', 'www.hafid.com', 1, 'admin@gmail.com', '2025-08-30 13:30:52', NULL, 'SKT.30082025133051', '2025-08-30', '2027-08-30'),
	('35a561ce-2090-4658-be71-24b446e4ae59', 'VMS.Pengajuan.21112025090753', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-21 09:07:53', '8d1eba75-0462-4ceb-a0e2-eea25164a38b', 'Pabrik', 'jl gunung pajeleran sukahati', 'Pondasi', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '081522667979', 'Hafid', 'hafidhanafid@gmail.com', '081522667979', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('3884f189-b6f7-4989-a26a-4ef4c226a532', 'VMS.Pengajuan.20112025141548', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-20 14:15:48', '5dfcd29b-8995-4abb-899f-062ec123662c', 'Pabrik', 'test', 'Pekerjaan tanah', 'test', 'Hafid', 'hafidhanafid@gmail.com', '081384456729', 'Hafid', 'hafidhanafid@gmail.com', '081384456729', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('411f383a-9e44-4506-822e-804c70849094', 'VMS.Pengajuan.20112025140139', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-20 14:01:39', '5dfcd29b-8995-4abb-899f-062ec123662c', 'Sub Kontraktor', 'jl gunung pajeleran sukahati', 'Pekerjaan tanah', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '081384456729', 'Hafid', 'hafidhanafid@gmail.com', '081384456729', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('45ae9643-aa5c-49cb-b6b9-1539626754fb', 'VMS.Pengajuan.20112025142752', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-20 14:27:53', '8d1eba75-0462-4ceb-a0e2-eea25164a38b', 'Distributor', 'jl gunung pajeleran sukahati', 'Pekerjaan tanah', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '081384456729', 'Hafid', 'hafidhanafid@gmail.com', '081384456729', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('4b229d36-306e-4910-8569-ff6ddbef7da8', 'VMS.Pengajuan.21112025090309', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-21 09:03:09', '8d1eba75-0462-4ceb-a0e2-eea25164a38b', 'Pabrik', 'jl gunung pajeleran sukahati', 'Pondasi', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '081522667979', 'Hafid', 'hafidhanafid@gmail.com', '081522667979', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('63ea5369-7580-40e7-9062-cc46a60ece1c', 'VMS.Pengajuan.20112025144252', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-20 14:42:52', '8d1eba75-0462-4ceb-a0e2-eea25164a38b', 'Pabrik', 'jl gunung pajeleran sukahati', 'Pondasi', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '081522667979', 'Hafid', 'hafidhanafid@gmail.com', '081522667979', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('75015b08-e31e-4cbe-a7ab-487e52eb3191', 'VMS.Pengajuan.21112025084228', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-21 08:42:28', '5dfcd29b-8995-4abb-899f-062ec123662c', 'Distributor', 'jl gunung pajeleran sukahati', 'Pondasi', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '081456437299', 'Hafid', 'hafidhanafid@gmail.com', '089092800912', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('822a1822-761f-4992-b380-afee7786b38a', 'VMS.Pengajuan.20112025135811', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-20 13:58:11', '7d87c121-fdc8-4346-aa9a-7f27b65ed80a', 'Sub Kontraktor', 'test', 'Pekerjaan tanah', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '081384456729', 'Hafid', 'hafidhanafid@gmail.com', '081384456729', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('846a5f1f-4ed0-40fe-96d0-15e2a7270010', 'VMS.Pengajuan.21112025152819', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-21 15:28:20', '8d1eba75-0462-4ceb-a0e2-eea25164a38b', 'Pabrik', 'jl gunung pajeleran sukahati', 'Pondasi', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '081522667979', 'Hafid', 'hafidhanafid@gmail.com', '0814564142516', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('a0b10ce0-bd2f-4cfb-8f73-ab26a074d899', 'VMS.Pengajuan.20112025144514', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-20 14:45:14', '5dfcd29b-8995-4abb-899f-062ec123662c', 'Pabrik', 'jl gunung pajeleran sukahati', 'Pondasi', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '081522667979', 'Hafid', 'hafidhanafid@gmail.com', '081522667979', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('b21b80ee-bf90-45ac-b538-6756e0d644fb', 'VMS.Pengajuan.20112025140020', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-20 14:00:21', '5dfcd29b-8995-4abb-899f-062ec123662c', 'Sub Kontraktor', 'jl gunung pajeleran sukahati', 'Pekerjaan tanah', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '081384456729', 'Hafid', 'hafidhanafid@gmail.com', '081384456729', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL),
	('fb95cb46-b71c-4c65-91d3-74ccadb83398', 'VMS.Pengajuan.03092025091415', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-09-03 12:34:03', '8d1eba75-0462-4ceb-a0e2-eea25164a38b', 'Distributor', 'jl gunung pajeleran sukahati', 'Pondasi', 'spesialisasi', 'test', 'test@gmail.com', '8637912871297', 'test', 'test@gmail.com', '13867912789129872', 'test', 1, 'hafidhanafid', '2025-09-11 14:06:26', 'reject', 'SKT.11092025140625', '2025-09-11', '2027-09-11'),
	('ff5bea46-7369-42a2-a1b9-b7470b784e07', 'VMS.Pengajuan.20112025144030', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'test', '2025-11-20 14:40:31', '5dfcd29b-8995-4abb-899f-062ec123662c', 'Pabrik', 'jl gunung pajeleran sukahati', 'Pondasi', 'Kontraktor', 'Hafid', 'hafidhanafid@gmail.com', '0723989731281', 'Hafid', 'hafidhanafid@gmail.com', '983208983208', 'www.hafid.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- Dumping structure for table vms.vms_vendor_access_dokumen
CREATE TABLE IF NOT EXISTS `vms_vendor_access_dokumen` (
  `id_access_dokumen` varchar(100) NOT NULL,
  `id_mst_dokumen` varchar(100) DEFAULT NULL,
  `id_mst_kualifikasi` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_access_dokumen`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.vms_vendor_access_dokumen: ~49 rows (approximately)
INSERT INTO `vms_vendor_access_dokumen` (`id_access_dokumen`, `id_mst_dokumen`, `id_mst_kualifikasi`) VALUES
	('078a3bde-c38d-4deb-b60a-79a3d1db5969', 'e2cdd388-5403-49d6-97a6-f5307bd96a24', '7d87c121-fdc8-4346-aa9a-7f27b65ed80a'),
	('1208aa76-8f78-47f4-a887-3288f0681df8', '36146bfe-1e01-4b51-82c0-989062b20acb', '5dfcd29b-8995-4abb-899f-062ec123662c'),
	('15e68206-65d9-4642-a511-c4cb939c586d', '9fd6deb7-6cb1-475d-ae8d-4ecc43b46952', '09fc1f25-b1fa-4ef3-a9b3-89b0960829b1'),
	('19f7d5d8-c56e-4e79-851d-5cf16743adb4', '61613730-4627-4a62-a6a0-6176e2537e1b', 'ee75e323-06b6-44ea-818d-47b8ab41a844'),
	('1b6b58db-c6de-485d-9a7e-d301fb8afd39', '7860743f-383f-474a-bd92-841bd87be862', '09fc1f25-b1fa-4ef3-a9b3-89b0960829b1'),
	('2407bd50-4f5d-48b7-9393-d02663d0c084', '4d2cf2df-0451-4160-956c-622be13e2a23', '7d87c121-fdc8-4346-aa9a-7f27b65ed80a'),
	('24d2d41a-8b27-4f97-9b85-56a1522e02fe', '4d2cf2df-0451-4160-956c-622be13e2a23', 'ee75e323-06b6-44ea-818d-47b8ab41a844'),
	('277d719b-1e36-4de8-b754-d6821b655531', 'e2cdd388-5403-49d6-97a6-f5307bd96a24', '8d1eba75-0462-4ceb-a0e2-eea25164a38b'),
	('2cb37512-0cea-4b44-a360-86abdf9c34a3', 'c02516ec-afd2-4882-87ef-79485746c4a8', 'f9778f98-d39e-4745-8cfb-09ddbc920b86'),
	('31426dd8-2afb-49ba-b134-ee1a9eb41091', '7860743f-383f-474a-bd92-841bd87be862', 'f9778f98-d39e-4745-8cfb-09ddbc920b86'),
	('36daa240-eb11-4479-b654-1b77e975ab25', '9ba17f1a-cd4f-4a17-817b-484a0925857f', 'ee75e323-06b6-44ea-818d-47b8ab41a844'),
	('38441ca5-6397-4c02-a6cd-a6b43c2ec62a', '61613730-4627-4a62-a6a0-6176e2537e1b', '8d1eba75-0462-4ceb-a0e2-eea25164a38b'),
	('3ba9ebf1-e60c-4074-916a-afa8082c0d18', 'de3533e6-d2b0-46e0-a1ac-4fec09f4a59e', 'f9778f98-d39e-4745-8cfb-09ddbc920b86'),
	('43f531c5-0a91-436c-8f87-3a1f3e862469', 'de3533e6-d2b0-46e0-a1ac-4fec09f4a59e', '7d87c121-fdc8-4346-aa9a-7f27b65ed80a'),
	('464e8db6-e445-4645-80d6-41e6312bc34e', '4d2cf2df-0451-4160-956c-622be13e2a23', '09fc1f25-b1fa-4ef3-a9b3-89b0960829b1'),
	('490d4bb1-20de-49c1-a4cd-7c6bf6a7c8ad', 'e2cdd388-5403-49d6-97a6-f5307bd96a24', '09fc1f25-b1fa-4ef3-a9b3-89b0960829b1'),
	('49534ea1-ba82-463b-8545-daf9df7179d1', 'e2cdd388-5403-49d6-97a6-f5307bd96a24', 'ee75e323-06b6-44ea-818d-47b8ab41a844'),
	('4a4271ee-16f8-46eb-ae6d-78cdcd43bf6f', '84ec35f7-36e8-4b6c-8f77-32c5df89e4d8', '8d1eba75-0462-4ceb-a0e2-eea25164a38b'),
	('56940008-3b09-4218-95a0-9fd8be450e68', '1e0c457a-74c1-4484-aa27-092ec5066741', 'ee75e323-06b6-44ea-818d-47b8ab41a844'),
	('5e6fb912-c3e9-4d80-9ba5-f7cf8944c6d3', '21fca75a-4627-483c-bb0c-b4624524ade4', '8d1eba75-0462-4ceb-a0e2-eea25164a38b'),
	('6e963a57-62b3-43c3-8918-2e8628c16a67', 'c02516ec-afd2-4882-87ef-79485746c4a8', '09fc1f25-b1fa-4ef3-a9b3-89b0960829b1'),
	('6ea739e0-18ce-4e1d-8311-0d9130fff263', '7681e5a9-62ac-41ab-99c6-ca01a1e1ab44', 'f9778f98-d39e-4745-8cfb-09ddbc920b86'),
	('722af339-f8ba-4250-926e-ff69815ceecd', '21fca75a-4627-483c-bb0c-b4624524ade4', '5dfcd29b-8995-4abb-899f-062ec123662c'),
	('75584a5e-892c-4397-aacd-180b5a642144', '4d2cf2df-0451-4160-956c-622be13e2a23', '5dfcd29b-8995-4abb-899f-062ec123662c'),
	('842203a6-e460-44a6-a77c-58ecdb50a3cd', '9fd6deb7-6cb1-475d-ae8d-4ecc43b46952', 'ee75e323-06b6-44ea-818d-47b8ab41a844'),
	('857411fe-1627-41a7-90f4-1fe573bf1662', '1e0c457a-74c1-4484-aa27-092ec5066741', 'f9778f98-d39e-4745-8cfb-09ddbc920b86'),
	('886089a2-6b13-485a-8d58-f0cb65fe20e2', '7860743f-383f-474a-bd92-841bd87be862', '7d87c121-fdc8-4346-aa9a-7f27b65ed80a'),
	('8d3e74f9-10ed-4b50-ab4b-2c0633bdb79f', '7860743f-383f-474a-bd92-841bd87be862', 'ee75e323-06b6-44ea-818d-47b8ab41a844'),
	('92f010a6-6ccf-4f5d-a5dd-8904df68c3c8', '1e0c457a-74c1-4484-aa27-092ec5066741', '7d87c121-fdc8-4346-aa9a-7f27b65ed80a'),
	('96a6b89f-aaa8-48b5-9700-f7081b7f7c81', '9fd6deb7-6cb1-475d-ae8d-4ecc43b46952', 'f9778f98-d39e-4745-8cfb-09ddbc920b86'),
	('9fa766c1-1e5c-46b2-9b6c-268c73fb1bac', '9ba17f1a-cd4f-4a17-817b-484a0925857f', '09fc1f25-b1fa-4ef3-a9b3-89b0960829b1'),
	('a8eb7c68-0e30-4002-b3b9-5a0311af00f3', 'c02516ec-afd2-4882-87ef-79485746c4a8', '7d87c121-fdc8-4346-aa9a-7f27b65ed80a'),
	('ac468d53-aa31-49a9-9dd0-298171c7eef3', '9ba17f1a-cd4f-4a17-817b-484a0925857f', 'f9778f98-d39e-4745-8cfb-09ddbc920b86'),
	('af25a4e7-d454-4549-b0ce-c4397b680037', '7681e5a9-62ac-41ab-99c6-ca01a1e1ab44', '7d87c121-fdc8-4346-aa9a-7f27b65ed80a'),
	('b782f344-778f-4f75-bb15-e7644bd82507', 'de3533e6-d2b0-46e0-a1ac-4fec09f4a59e', '09fc1f25-b1fa-4ef3-a9b3-89b0960829b1'),
	('bb9c7db3-f765-4338-9872-61c37a2ae652', '7681e5a9-62ac-41ab-99c6-ca01a1e1ab44', '09fc1f25-b1fa-4ef3-a9b3-89b0960829b1'),
	('be8ba04d-257f-45c5-b594-803b999b1088', '1e0c457a-74c1-4484-aa27-092ec5066741', '09fc1f25-b1fa-4ef3-a9b3-89b0960829b1'),
	('bef9a8f1-552c-497f-bd3c-d600a306f8da', '4d2cf2df-0451-4160-956c-622be13e2a23', '8d1eba75-0462-4ceb-a0e2-eea25164a38b'),
	('c12ff28c-1dc7-46c9-af7b-72ae8fc6fea5', '61613730-4627-4a62-a6a0-6176e2537e1b', '5dfcd29b-8995-4abb-899f-062ec123662c'),
	('c7da5391-030c-4db1-9665-191ce60d6f09', 'c02516ec-afd2-4882-87ef-79485746c4a8', 'ee75e323-06b6-44ea-818d-47b8ab41a844'),
	('d5bbc9ef-079e-48f1-b86a-77fc2496e5f1', '36146bfe-1e01-4b51-82c0-989062b20acb', '8d1eba75-0462-4ceb-a0e2-eea25164a38b'),
	('dc324999-f3ee-4b37-b87b-9bd49aa1ebf6', 'de3533e6-d2b0-46e0-a1ac-4fec09f4a59e', 'ee75e323-06b6-44ea-818d-47b8ab41a844'),
	('e63fd53a-99a1-4fe1-8584-5370a1b32331', 'e2cdd388-5403-49d6-97a6-f5307bd96a24', '5dfcd29b-8995-4abb-899f-062ec123662c'),
	('e66a7e24-f493-45eb-a34f-3903c31f8f14', '61613730-4627-4a62-a6a0-6176e2537e1b', '09fc1f25-b1fa-4ef3-a9b3-89b0960829b1'),
	('f043820f-198d-4254-b6c2-11d17cbee36f', '84ec35f7-36e8-4b6c-8f77-32c5df89e4d8', '5dfcd29b-8995-4abb-899f-062ec123662c'),
	('f220e691-16ac-4853-bb12-1d4a988fc7f4', '9fd6deb7-6cb1-475d-ae8d-4ecc43b46952', '7d87c121-fdc8-4346-aa9a-7f27b65ed80a'),
	('f4d24f79-49b8-4e31-af38-435b5c0bf0b3', '4d2cf2df-0451-4160-956c-622be13e2a23', 'f9778f98-d39e-4745-8cfb-09ddbc920b86'),
	('f676420e-c6b6-4951-b92a-36a138fb6933', '7681e5a9-62ac-41ab-99c6-ca01a1e1ab44', 'ee75e323-06b6-44ea-818d-47b8ab41a844'),
	('f7e1fc4e-8a13-4192-8703-3dde79b9dda0', 'e2cdd388-5403-49d6-97a6-f5307bd96a24', 'f9778f98-d39e-4745-8cfb-09ddbc920b86');

-- Dumping structure for table vms.vms_vendor_detail
CREATE TABLE IF NOT EXISTS `vms_vendor_detail` (
  `id_vendor_detail` varchar(100) NOT NULL,
  `id_vendor` varchar(100) DEFAULT NULL,
  `id_dokumen` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `nama_dokumen` varchar(100) DEFAULT NULL,
  `url_dokumen` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_vendor_detail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.vms_vendor_detail: ~17 rows (approximately)
INSERT INTO `vms_vendor_detail` (`id_vendor_detail`, `id_vendor`, `id_dokumen`, `nama_dokumen`, `url_dokumen`) VALUES
	('02775ecf-09c3-4bc9-b849-fca4d7e13061', '846a5f1f-4ed0-40fe-96d0-15e2a7270010', NULL, 'SKK / Sertifikasi', 'uploads/250af5c8-4693-43a3-8a53-344a1bf477a3.pdf'),
	('0d4ffdb0-8fad-4ba3-b071-4058510f70e9', '18c9c89e-eb76-4a13-996c-5174be5a5ec7', NULL, 'Compro', 'uploads\\0191991b-53ed-41c3-bc0b-c88179af51fe.pdf'),
	('11a517b4-d374-4c14-9784-2ceb56c8d06b', '18c9c89e-eb76-4a13-996c-5174be5a5ec7', NULL, 'NIB', 'uploads\\3355af15-2fb4-41d5-8e50-ba0f8f14f83c.pdf'),
	('239b2dfb-705a-4f7a-b1fe-11ee7d9ab320', 'fb95cb46-b71c-4c65-91d3-74ccadb83398', NULL, 'KTP', 'uploads/8ce59a6a-8041-4566-a225-a7bdaf7fb683.pdf'),
	('27576644-501c-4274-9c7c-bf8857811370', '18c9c89e-eb76-4a13-996c-5174be5a5ec7', NULL, 'KTP Direktur Utama', 'uploads\\cab7cc5f-2e4f-4335-b4b3-b6f239abc82c.pdf'),
	('32fdc763-0310-4182-bc91-046fbafd3686', '15c2a2a7-7aba-43eb-86a1-e08b6291a8f0', NULL, 'SKK / Sertifikasi', 'uploads/7aa5fbbc-b01e-4db6-8858-33c9c67206ec.pdf'),
	('390e3dd5-09a6-45e3-8fcf-6d677e6a1039', '846a5f1f-4ed0-40fe-96d0-15e2a7270010', NULL, 'SKK / Sertifikasi', 'uploads/2a872e12-1ad4-4d87-a7aa-67d3956d4617.pdf'),
	('3eea01a7-232c-4fcf-81ba-92032dd46e13', '846a5f1f-4ed0-40fe-96d0-15e2a7270010', NULL, 'KTP', 'uploads/3ceb826b-ee5e-45a5-9d57-fe98b86e7d2e.pdf'),
	('5afe5254-4635-4efe-aba1-a0d48dc3cc8f', '846a5f1f-4ed0-40fe-96d0-15e2a7270010', NULL, 'KTP', 'uploads/81127cc1-0676-4293-b0c9-8214c304bfa4.pdf'),
	('65e30e64-7614-413e-8207-e1af3560e5e3', '18c9c89e-eb76-4a13-996c-5174be5a5ec7', NULL, 'Akta Pendirian / Perubahan', 'uploads\\5f477099-fce7-4760-9b6e-a2593601c386.pdf'),
	('7d289da0-6771-4b5d-a93f-003ace8edfa4', '18c9c89e-eb76-4a13-996c-5174be5a5ec7', NULL, 'SPPKP', 'uploads\\88e3d0f3-02d8-489a-a017-eec9c146181d.pdf'),
	('803657e6-3ab1-4d26-871e-7ef1070a79db', '18c9c89e-eb76-4a13-996c-5174be5a5ec7', NULL, 'SBUJK / SBU', 'uploads\\d8ac5cf9-671c-403c-8f84-648936cad061.pdf'),
	('963b6448-5911-492c-a135-887b4949e989', '15c2a2a7-7aba-43eb-86a1-e08b6291a8f0', NULL, 'SKK / Sertifikasi', 'uploads/e901eff6-8629-4a86-884f-79ec730c3da6.pdf'),
	('b9d961b4-04bc-4fa8-afeb-9d3dd1192642', '15c2a2a7-7aba-43eb-86a1-e08b6291a8f0', NULL, 'SKK / Sertifikasi', 'uploads/25a7db4b-36ba-44af-8f68-49ccf8180f73.pdf'),
	('d148a77d-acaf-4fc2-bbd4-037949d295be', '18c9c89e-eb76-4a13-996c-5174be5a5ec7', NULL, 'NPWP Perusahaan', 'uploads\\3d6b0660-e4b1-4e44-9719-a116c994cf0d.pdf'),
	('dabd46f5-153c-4a19-9504-27ebfbc64fd0', '2bc567f4-a4fe-4158-b7ec-190ea8fb24cb', NULL, 'KTP', 'uploads/5ee08f38-9efb-4b28-8f19-56336c5713b2.pdf'),
	('fd269539-71dd-4e00-ac57-7c15c9c19f73', '18c9c89e-eb76-4a13-996c-5174be5a5ec7', NULL, 'SKK / Sertifikasi', 'uploads\\86a8c67b-747f-4a49-9670-ed708fda4dbf.pdf');

-- Dumping structure for table vms.vms_vendor_mst_dokumen
CREATE TABLE IF NOT EXISTS `vms_vendor_mst_dokumen` (
  `id_mst_dokumen` varchar(100) NOT NULL,
  `kode_dokumen` varchar(100) DEFAULT NULL,
  `nama_dokumen` varchar(100) DEFAULT NULL,
  `required` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_mst_dokumen`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.vms_vendor_mst_dokumen: ~14 rows (approximately)
INSERT INTO `vms_vendor_mst_dokumen` (`id_mst_dokumen`, `kode_dokumen`, `nama_dokumen`, `required`) VALUES
	('1e0c457a-74c1-4484-aa27-092ec5066741', '07', 'Akta Pendirian / Perubahan', 1),
	('21fca75a-4627-483c-bb0c-b4624524ade4', '03', 'Nomor Pokok Wajib pajak (NPWP)', 0),
	('36146bfe-1e01-4b51-82c0-989062b20acb', '02', 'Kartu Keluarga (KK)', 0),
	('3b4fbdef-5cd9-4091-8dd2-6d36cd4c404b', '10', 'ISO', 0),
	('4d2cf2df-0451-4160-956c-622be13e2a23', '15', 'SKK / Sertifikasi', 1),
	('61613730-4627-4a62-a6a0-6176e2537e1b', '17', 'Dokumen Pendukung Lainnya', 0),
	('7681e5a9-62ac-41ab-99c6-ca01a1e1ab44', '08', 'NIB', 1),
	('7860743f-383f-474a-bd92-841bd87be862', '09', 'SBUJK / SBU', 0),
	('84ec35f7-36e8-4b6c-8f77-32c5df89e4d8', '01', 'KTP', 0),
	('9ba17f1a-cd4f-4a17-817b-484a0925857f', '12', 'SK Kemenkumham', 0),
	('9fd6deb7-6cb1-475d-ae8d-4ecc43b46952', '11', 'Compro', 0),
	('c02516ec-afd2-4882-87ef-79485746c4a8', '14', 'KTP Direktur Utama', 1),
	('de3533e6-d2b0-46e0-a1ac-4fec09f4a59e', '13', 'NPWP Perusahaan', 1),
	('e2cdd388-5403-49d6-97a6-f5307bd96a24', '16', 'SPPKP', 0);

-- Dumping structure for table vms.vms_vendor_mst_kualifikasi
CREATE TABLE IF NOT EXISTS `vms_vendor_mst_kualifikasi` (
  `id_mst_kualifikasi` varchar(100) NOT NULL,
  `kualifikasi` varchar(100) DEFAULT NULL,
  `no_urut` int DEFAULT NULL,
  PRIMARY KEY (`id_mst_kualifikasi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.vms_vendor_mst_kualifikasi: ~6 rows (approximately)
INSERT INTO `vms_vendor_mst_kualifikasi` (`id_mst_kualifikasi`, `kualifikasi`, `no_urut`) VALUES
	('09fc1f25-b1fa-4ef3-a9b3-89b0960829b1', 'Sangat Besar', 6),
	('5dfcd29b-8995-4abb-899f-062ec123662c', 'Kecil Non Badan Usaha', 2),
	('7d87c121-fdc8-4346-aa9a-7f27b65ed80a', 'Kecil Badan Usaha', 3),
	('8d1eba75-0462-4ceb-a0e2-eea25164a38b', 'Pribadi / Non Badan Usaha', 1),
	('ee75e323-06b6-44ea-818d-47b8ab41a844', 'Besar', 5),
	('f9778f98-d39e-4745-8cfb-09ddbc920b86', 'Menengah', 4);

-- Dumping structure for table vms.vms_vendor_update
CREATE TABLE IF NOT EXISTS `vms_vendor_update` (
  `id_vendor_update` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `id_vendor` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `id_pengajuan` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `id_user` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `nama_perusahaan` varchar(100) DEFAULT NULL,
  `tanggal_pengajuan` datetime DEFAULT NULL,
  `id_kualifikasi_usaha` varchar(100) DEFAULT NULL,
  `klasifikasi_usaha` varchar(100) DEFAULT NULL,
  `alamat_perusahaan` text,
  `kategori` varchar(100) DEFAULT NULL,
  `spesialisasi` varchar(100) DEFAULT NULL,
  `nama_pic` varchar(100) DEFAULT NULL,
  `email_pic` varchar(100) DEFAULT NULL,
  `no_hp_pic` varchar(100) DEFAULT NULL,
  `nama_direktur` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `email_direktur` varchar(100) DEFAULT NULL,
  `no_hp_direktur` varchar(100) DEFAULT NULL,
  `website` varchar(100) DEFAULT NULL,
  `isApproval` int DEFAULT NULL,
  `approvedBy` varchar(100) DEFAULT NULL,
  `approvedAt` datetime DEFAULT NULL,
  `catatan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `no_skt` varchar(100) DEFAULT NULL,
  `tanggal_awal_skt` date DEFAULT NULL,
  `tanggal_akhir_skt` date DEFAULT NULL,
  `alasan_update` text,
  PRIMARY KEY (`id_vendor_update`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table vms.vms_vendor_update: ~8 rows (approximately)
INSERT INTO `vms_vendor_update` (`id_vendor_update`, `id_vendor`, `id_pengajuan`, `id_user`, `nama_perusahaan`, `tanggal_pengajuan`, `id_kualifikasi_usaha`, `klasifikasi_usaha`, `alamat_perusahaan`, `kategori`, `spesialisasi`, `nama_pic`, `email_pic`, `no_hp_pic`, `nama_direktur`, `email_direktur`, `no_hp_direktur`, `website`, `isApproval`, `approvedBy`, `approvedAt`, `catatan`, `no_skt`, `tanggal_awal_skt`, `tanggal_akhir_skt`, `alasan_update`) VALUES
	('3e2d14ad-9b93-4d32-a043-cd22ceb404ec', '2bc567f4-a4fe-4158-b7ec-190ea8fb24cb', 'VMS.update.08092025160122', '638d7eac-110d-46d1-af4a-60509fde2a9c', NULL, '2025-09-08 16:01:22', NULL, 'Pabrik', 'jl gunung pajeleran sukahati BEckup', 'Pekerjaan tanah', 'Kontraktor', 'Hafid', 'hafid@gmail.com', '0723989731281', 'Hafid', 'hafid@gmail.com', '983208983208', 'www.hafid.com', 1, 'admin@gmail.com', '2025-09-08 16:01:30', NULL, NULL, NULL, NULL, 'alamat vendor'),
	('484b07ae-62f4-46e7-a2c1-b091d3a5ea9d', '2bc567f4-a4fe-4158-b7ec-190ea8fb24cb', 'VMS.update.08092025143941', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'PT Pilar innovasi mandiri', '2025-09-08 14:39:42', NULL, 'Pabrik', 'jl gunung pajeleran sukahati Be', 'Pekerjaan tanah', 'Kontraktor', 'Hafid', 'hafid@gmail.com', '0723989731281', 'Hafid', 'hafid@gmail.com', '983208983208', 'www.hafid.com', 1, 'admin@gmail.com', '2025-09-08 15:45:39', NULL, NULL, NULL, NULL, NULL),
	('4935675c-1efc-466b-85ac-d210a68e760e', '2bc567f4-a4fe-4158-b7ec-190ea8fb24cb', 'VMS.update.11092025134826', '638d7eac-110d-46d1-af4a-60509fde2a9c', NULL, '2025-09-11 13:48:27', NULL, 'Pabrik', 'jl gunung pajeleran sukahati BEckup', 'Pekerjaan tanah', 'Kontraktor, test', 'Hafid', 'hafid@gmail.com', '0723989731281', 'Hafid', 'hafid@gmail.com', '983208983208', 'www.hafid.com', 1, 'admin@gmail.com', '2025-09-11 13:48:36', NULL, NULL, NULL, NULL, 'spesialisasi'),
	('6fbd34f3-7dac-4cc8-881e-5bd00cec8d83', '2bc567f4-a4fe-4158-b7ec-190ea8fb24cb', 'VMS.update.11092025135134', '638d7eac-110d-46d1-af4a-60509fde2a9c', NULL, '2025-09-11 13:51:34', NULL, 'Pabrik', 'jl gunung pajeleran sukahati BEckup, test', 'Pekerjaan tanah', 'Kontraktor, testter, akuisisi', 'Hafid', 'hafid@gmail.com', '0723989731281', 'Hafid', 'hafid@gmail.com', '983208983208', 'www.hafid.com', 1, 'admin@gmail.com', '2025-09-11 13:51:49', NULL, NULL, NULL, NULL, 'test'),
	('8e6fec86-ffbb-42e1-8724-5b245df726ce', '2bc567f4-a4fe-4158-b7ec-190ea8fb24cb', 'VMS.update.08092025143854', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'PT Pilar innovasi mandiri', '2025-09-08 14:38:55', NULL, 'Pabrik', 'jl gunung pajeleran sukahati Be', 'Pekerjaan tanah', 'Kontraktor', 'Hafid', 'hafid@gmail.com', '0723989731281', 'Hafid', 'hafid@gmail.com', '983208983208', 'www.hafid.com', 1, 'admin@gmail.com', '2025-09-08 15:46:09', NULL, NULL, NULL, NULL, NULL),
	('bc672cf5-d767-41d2-9b49-6cb721b4eb9f', '2bc567f4-a4fe-4158-b7ec-190ea8fb24cb', 'VMS.update.08092025152002', '638d7eac-110d-46d1-af4a-60509fde2a9c', NULL, '2025-09-08 15:20:02', NULL, 'Pabrik', 'jl gunung pajeleran sukahati BE', 'Pekerjaan tanah', 'Kontraktor', 'Hafid', 'hafid@gmail.com', '0723989731281', 'Hafid', 'hafid@gmail.com', '983208983208', 'www.hafid.com', 1, 'admin@gmail.com', '2025-09-08 15:46:26', NULL, NULL, NULL, NULL, 'perubahan di alamat perusahaan'),
	('d179f820-54be-4b4c-94af-89a4244b6562', '2bc567f4-a4fe-4158-b7ec-190ea8fb24cb', 'VMS.update.08092025144119', '638d7eac-110d-46d1-af4a-60509fde2a9c', 'PT Pilar innovasi mandiri', '2025-09-08 14:41:20', NULL, 'Pabrik', 'jl gunung pajeleran sukahati BE', 'Pekerjaan tanah', 'Kontraktor', 'Hafid', 'hafid@gmail.com', '0723989731281', 'Hafid', 'hafid@gmail.com', '983208983208', 'www.hafid.com', 1, 'admin@gmail.com', '2025-09-08 16:00:33', NULL, NULL, NULL, NULL, NULL),
	('d5050d9b-f2e1-413c-b900-ec0636d2bbee', '2bc567f4-a4fe-4158-b7ec-190ea8fb24cb', 'VMS.update.11092025135040', '638d7eac-110d-46d1-af4a-60509fde2a9c', NULL, '2025-09-11 13:50:41', NULL, 'Pabrik', 'jl gunung pajeleran sukahati BEckup', 'Pekerjaan tanah', 'Kontraktor, testter', 'Hafid', 'hafid@gmail.com', '0723989731281', 'Hafid', 'hafid@gmail.com', '983208983208', 'www.hafid.com', 1, 'admin@gmail.com', '2025-09-11 13:50:46', NULL, NULL, NULL, NULL, 'test');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
