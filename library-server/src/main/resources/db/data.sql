USE library;

-- 默认角色
INSERT INTO role (role_code, role_name, description) VALUES
('ROLE_READER', '读者', '普通读者，查询图书、续借、预约'),
('ROLE_LIBRARIAN', '图书管理员', '图书编目、借还操作、读者管理'),
('ROLE_ADMIN', '超级管理员', '系统管理、用户管理、权限分配');

-- 默认超级管理员 (密码: admin123, BCrypt hash placeholder — Task 4 will replace with real hash or add CommandLineRunner)
INSERT INTO user (username, password, real_name, status) VALUES
('admin', 'TO_BE_REPLACED_BY_INIT_RUNNER', '系统管理员', 1);

-- 管理员关联角色
INSERT INTO user_role (user_id, role_id) VALUES (1, 3);

-- 中图分类（财经类高校常用分类）
INSERT INTO category (code, name, parent_id) VALUES
('A', '马克思主义、列宁主义、毛泽东思想、邓小平理论', 0),
('B', '哲学、宗教', 0),
('C', '社会科学总论', 0),
('D', '政治、法律', 0),
('F', '经济', 0),
('F0', '经济学', 5),
('F23', '会计', 5),
('F27', '企业经济', 5),
('F7', '贸易经济', 5),
('F8', '财政、金融', 5),
('F81', '财政、国家财政', 10),
('F83', '金融、银行', 10),
('G', '文化、科学、教育、体育', 0),
('H', '语言、文字', 0),
('I', '文学', 0),
('K', '历史、地理', 0),
('T', '工业技术', 0),
('TP3', '计算技术、计算机技术', 17);

-- 默认菜单
INSERT INTO menu (id, name, path, component, icon, parent_id, sort, permission, type) VALUES
(1, '工作台', '/dashboard', 'dashboard/index', 'HomeFilled', 0, 1, NULL, 1),
(2, '图书管理', '/book', NULL, 'Reading', 0, 2, NULL, 0),
(3, '图书编目', '/book/catalog', 'book/catalog/index', NULL, 2, 1, 'book:catalog', 1),
(4, '馆藏查询', '/book/list', 'book/list/index', NULL, 2, 2, 'book:list', 1),
(5, '库存盘点', '/book/inventory', 'book/inventory/index', NULL, 2, 3, 'book:inventory', 1),
(6, '借阅管理', '/borrow', NULL, 'Notebook', 0, 3, NULL, 0),
(7, '借书操作', '/borrow/borrow', 'borrow/borrow/index', NULL, 6, 1, 'borrow:create', 1),
(8, '还书操作', '/borrow/return', 'borrow/return/index', NULL, 6, 2, 'borrow:return', 1),
(9, '续借管理', '/borrow/renew', 'borrow/renew/index', NULL, 6, 3, 'borrow:renew', 1),
(10, '预约管理', '/borrow/reserve', 'borrow/reserve/index', NULL, 6, 4, 'borrow:reserve', 1),
(11, '逾期处理', '/borrow/overdue', 'borrow/overdue/index', NULL, 6, 5, 'borrow:overdue', 1),
(12, '读者管理', '/reader', NULL, 'User', 0, 4, NULL, 0),
(13, '读者列表', '/reader/list', 'reader/list/index', NULL, 12, 1, 'reader:list', 1),
(14, '读者注册', '/reader/register', 'reader/register/index', NULL, 12, 2, 'reader:register', 1),
(15, '借阅证管理', '/reader/card', 'reader/card/index', NULL, 12, 3, 'reader:card', 1),
(16, '统计分析', '/statistics', NULL, 'DataAnalysis', 0, 5, NULL, 0),
(17, '借阅统计', '/statistics/borrow', 'statistics/borrow/index', NULL, 16, 1, 'statistics:borrow', 1),
(18, '热门图书', '/statistics/popular', 'statistics/popular/index', NULL, 16, 2, 'statistics:popular', 1),
(19, '读者统计', '/statistics/reader', 'statistics/reader/index', NULL, 16, 3, 'statistics:reader', 1),
(20, '系统管理', '/system', NULL, 'Setting', 0, 6, NULL, 0),
(21, '用户管理', '/system/user', 'system/user/index', NULL, 20, 1, 'system:user', 1),
(22, '角色管理', '/system/role', 'system/role/index', NULL, 20, 2, 'system:role', 1),
(23, '操作日志', '/system/log', 'system/log/index', NULL, 20, 3, 'system:log', 1);

-- 为超级管理员分配所有菜单
INSERT INTO role_menu (role_id, menu_id)
SELECT 3, id FROM menu;
