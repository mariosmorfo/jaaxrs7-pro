package gr.aueb.cf.schoolapp.rest;

import gr.aueb.cf.schoolapp.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.schoolapp.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.schoolapp.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.schoolapp.dto.*;
import gr.aueb.cf.schoolapp.mapper.Mapper;
import gr.aueb.cf.schoolapp.service.ITeacherService;
import gr.aueb.cf.schoolapp.validator.ValidatorUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;

import javax.print.attribute.standard.Media;
import java.net.URI;
import java.util.List;
import java.util.Map;


@ApplicationScoped

@Path("/teachers")
public class TeacherRestController {


    private ITeacherService teacherService;



    @Inject
    public TeacherRestController(ITeacherService teacherService) {
        this.teacherService = teacherService;
    }



    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTeacher(TeacherInsertDTO insertDTO, @Context UriInfo uriInfo)
            throws EntityInvalidArgumentException, EntityAlreadyExistsException {

        List<String> errors = ValidatorUtil.validateDTO(insertDTO);
        if (!errors.isEmpty()) {
            throw new EntityInvalidArgumentException("Teacher", String.join("\n", errors));
        }

        TeacherReadOnlyDTO readOnlyDTO = teacherService.insertTeacher(insertDTO);
        URI newResourceUri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(readOnlyDTO.getId()))
                .build();

        return Response.created(newResourceUri).entity(readOnlyDTO).build();
    }

    @Path("/{teacherId}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTeacher(@PathParam("teacherId") Long teacherId, TeacherUpdateDTO updateDTO)
            throws EntityInvalidArgumentException, EntityNotFoundException {

        List<String> errors = ValidatorUtil.validateDTO(updateDTO);
        if (!errors.isEmpty()) {
            throw new EntityInvalidArgumentException("Teacher", String.join("\n", errors));
        }

        TeacherReadOnlyDTO readOnlyDTO = teacherService.updateTeacher(updateDTO);
        return Response.status(Response.Status.OK)
                .entity(readOnlyDTO)
                .build();
    }

    @DELETE
    @Path("/{teacherId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTeacher(@PathParam("teacherId") Long teacherId)
            throws EntityNotFoundException{

        TeacherReadOnlyDTO readOnlyDTO = teacherService.getTeacherById(teacherId);
         teacherService.deleteTeacher(teacherId);
         return Response.status(Response.Status.OK)
                 .entity(readOnlyDTO)
                 .build();
    }

    @GET
    @Path("/{teacherId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeacher(@PathParam("teacherId") Long id)
            throws EntityNotFoundException{
        TeacherReadOnlyDTO readOnlyDTO = teacherService.getTeacherById(id);
        return Response.status(Response.Status.OK)
                .entity(readOnlyDTO)
                .build();
    }

    @GET
    @Path("/filtered")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFiltered(@QueryParam("firstname") @DefaultValue("") String firstname,
                                @QueryParam("lastname") @DefaultValue("") String lastname,
                                @QueryParam("vat") @DefaultValue("") String vat){
       TeacherFiltersDTO filtersDTO = new TeacherFiltersDTO(firstname, lastname, vat);
       Map<String, Object> criteria;

       criteria = Mapper.mapToCriteria(filtersDTO);
       List<TeacherReadOnlyDTO> readOnlyDTOs = teacherService.getTeachersByCriteria(criteria);

       return Response.status(Response.Status.OK)
               .entity(readOnlyDTOs)
               .build();
    }

    @GET
    @Path("/paginated")
    @Produces(MediaType.APPLICATION_JSON)
    public PaginatedResult<TeacherReadOnlyDTO> getFilteredPaginated(@QueryParam("firstname") @DefaultValue("") String firstname,
                                                                    @QueryParam("lastname") @DefaultValue("") String lastname,
                                                                    @QueryParam("vat") @DefaultValue("") String vat,
                                                                    @QueryParam("page") @DefaultValue("0") Integer page,
                                                                    @QueryParam("size") @DefaultValue("10") Integer size)
            throws EntityInvalidArgumentException
    {
        TeacherFiltersDTO filtersDTO = new TeacherFiltersDTO(firstname, lastname, vat);
        Map<String , Object> criteria = Mapper.mapToCriteria(filtersDTO);

        if(page<0) throw new EntityInvalidArgumentException("PageInvalidNumber", "Invalid page number");
        if(size<=0) throw new EntityInvalidArgumentException("PageInvalidSize", "Invalid page size");

        List<TeacherReadOnlyDTO> readOnlyDTOs = teacherService.getTeachersByCriteriaPaginated(criteria, size, page);
        long totalItems = teacherService.getTeachersCountByCriteria(criteria);
        int totalPages = (int) Math.ceil( (double) totalItems/size);

        return new PaginatedResult<>(
                readOnlyDTOs,
                page,
                size,
                totalPages,
                totalItems
        );


    }

}
