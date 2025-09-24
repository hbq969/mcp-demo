insert into books(id,title,category,author,publication_date,isbn) values (1,'Spring实战（第6版）','编程','Craig Walls','2022-01-15','9787115582247');
insert into books(id,title,category,author,publication_date,isbn) values (2,'深入理解Java虚拟机','编程','周志明','2019-12-01','9787111641247');
insert into books(id,title,category,author,publication_date,isbn) values (3,'Java编程思想（第4版）','编程','Bruce Eckel','2007-06-01','9787111213826');
insert into books(id,title,category,author,publication_date,isbn) values (4,'算法（第4版）','计算机科学','Robert Sedgewick','2012-10-01','9787115293800');
insert into books(id,title,category,author,publication_date,isbn) values (5,'云原生架构','架构设计','张三','2023-03-15','9781234567890');
insert into books(id,title,category,author,publication_date,isbn) values (6,'微服务设计模式','架构设计','李四','2021-08-20','9789876543210');
insert into books(id,title,category,author,publication_date,isbn) values (7,'领域驱动设计','架构设计','Eric Evans','2010-04-10','9787111214748');
insert into books(id,title,category,author,publication_date,isbn) values (8,'高性能MySQL','数据库','Baron Schwartz','2013-05-25','9787111464747');
insert into books(id,title,category,author,publication_date,isbn) values (9,'Redis实战','数据库','Josiah L. Carlson','2015-09-30','9787115419378');
insert into books(id,title,category,author,publication_date,isbn) values (10,'深入浅出Docker','容器技术','王五','2022-11-20','9787123456789');

insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('demo','MCP','人工智能','/mcp','-',0,1,'AI1',1735800456);
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('demo','mcp_demo','MCP演示','inner:/demo/mcp-ui/index.html#/mcp','MCP',0,2,'AI2',1735800456);
insert into h_menus(app,name,menu_desc,url,parent_key,order_index,menu_level,icon_name,created_at) values('demo','RAG','RAG演示','inner:/demo/mcp-ui/index.html#/rag','MCP',1,2,'HAProxyIcon',1735800456);