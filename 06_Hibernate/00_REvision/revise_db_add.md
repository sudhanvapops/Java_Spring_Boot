public static void test(){
    
    // ! 1. Configuration
    Configuration cfg = new Configuration();
    cfg.addAnnotatedClass(com.orm.model.Student.class);
    cfg.configure();

    // ! 2. Make Session Factory
    SessionFactory sf = cfg.buildSessionFactory();

    // Till Up only once 

    // !  Data to be added
    Student s1  = new Student();
    s1.setName("Aka");
    s1.setRollNo(4);
    s1.setsAge(21);

    // When ever you need to add just below code

    // ! Open the session
    Session session = sf.openSession();
    // ! Set a Transaction
    Transaction trx = session.beginTransaction();
    session.persist(s1);
    // Commit transaction
    trx.commit();

    // ! Close Connection
    session.close();
    sf.close();

}
