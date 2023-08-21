package fpt.aptech.portal.entities;


public class UsersAndCompanyDTO {
    Company company;
    Users user;

    public UsersAndCompanyDTO() {
    }

    public UsersAndCompanyDTO(Company company, Users user) {
        this.company = company;
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
    
    
}
