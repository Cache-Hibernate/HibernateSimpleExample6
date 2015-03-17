package hibernate.test;

import hibernate.test.dto.DepartmentEntity;

import org.hibernate.Session;

/**
 * {@link http://howtodoinjava.com/2013/07/01/understanding-hibernate-first-level-cache-with-example/}
 ** {@link http://howtodoinjava.com/2013/07/04/hibernate-ehcache-configuration-tutorial/}
 * *************************************************************************************
 * ehcache6 | DEPARTMENT,EMPLOYEE
 *
 * "JPA" - это интерфейс, который реализуется с помощью других ORM.
 * Эти - "ORM" (Object/Relational Mapping) - выступают в качестве поставщика для этого, это способ сохранения объектов в реляционную базу данных.
 *
 * "Entity Manager" - является частью спецификации JPA для выполнения доступа к базе данных с помощью управляемых объектов.
 * "Hibernate"      - это один из самых популярных на сегодняшний день ORM-фреймворков
 * "Hibernate" кэш-памяти первого уровня-cache - чтобы вернуть кэшированные <hibernate лиц>.
 * *************************************************************************************
 */
public class TestHibernateEhcache {
	public static void main(String[] args) {
		storeData();
		
		try{
			// Open the hibernate session (открываем 'hibernate'-сессию)
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			
			// fetch the department entity from database first time (впервые извлечь объект-'department' из базы данных)
			DepartmentEntity department = (DepartmentEntity) session.load(DepartmentEntity.class, new Integer(1));
			System.out.println("впервые извлечь объект-'department' из базы данных >> " + department.getName());
			
			// fetch the department entity again; Fetched from first level cache (снова извлечь объект-'department' - извлекается из кэша первого уровне)
			department = (DepartmentEntity) session.load(DepartmentEntity.class, new Integer(1));
			System.out.println("снова извлечь объект-'department' - извлекается из кэша первого уровне >> " + department.getName());
			
			// Let's close the session (давайте закроем сессию)
			session.getTransaction().commit();
			session.close();
			
			// Try to get department in new session (попытайтесь получить объект-'department' в новой сессии)
			Session anotherSession = HibernateUtil.getSessionFactory().openSession();
			anotherSession.beginTransaction();
            System.out.println("попытайтесь получить объект-'department' в новой сессии");
			
			// Here entity is already in second level cache so no database query will be hit (Сущность уже находится в кэш-памяти второго уровня, так запрос к базе данных не будет выполнен)
			department = (DepartmentEntity) anotherSession.load(DepartmentEntity.class, new Integer(1));
			System.out.println("Сущность уже находится в кэш-памяти второго уровня, так запрос к базе данных не будет выполнен >> " + department.getName());
			
			anotherSession.getTransaction().commit();
			anotherSession.close();
		} finally {
			System.out.println(HibernateUtil.getSessionFactory().getStatistics().getEntityFetchCount()); // Prints 1
			System.out.println(HibernateUtil.getSessionFactory().getStatistics().getSecondLevelCacheHitCount()); // Prints 1
			
			HibernateUtil.shutdown();
		}
	}
	
	private static void storeData(){
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		DepartmentEntity department = new DepartmentEntity();
		department.setName("Human Resource");
		
		session.save(department);
		session.getTransaction().commit();
	}
}
