package com.knowly.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.knowly.dto.SearchProfileDto;
import com.knowly.dto.SkillDto;
import com.knowly.entity.Skill;
import com.knowly.entity.User;
import com.knowly.entity.UserProfile;
import com.knowly.exceptions.InvalidSkillException;
import com.knowly.exceptions.SkillAlreadyExistsException;
import com.knowly.exceptions.UserNotFoundException;
import com.knowly.mapper.SearchProfileMapper;
import com.knowly.mapper.SkillMapper;
import com.knowly.repository.ProfileRepository;
import com.knowly.repository.SkillRepository;
import com.knowly.repository.UserRepository;
import com.knowly.util.OverAllRating;
import com.knowly.util.SearchKeyUtil;

@Service
public class SkillService {
	private SkillRepository skillRepo;
	private UserRepository userRepo;
	private ProfileRepository profileRepo;

	public SkillService(SkillRepository skillRepo, UserRepository userRepo, ProfileRepository profileRepo) {
		super();
		this.skillRepo = skillRepo;
		this.userRepo = userRepo;
		this.profileRepo = profileRepo;
	}

	@Transactional
	public void save(SkillDto dto,String email) {
		User user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("User not Found"));
		if (dto.getName() != null && !dto.getName().isBlank()) {
			// Update existing skill if id is present
			if (dto.getId() != null && !dto.getId().isEmpty()) {
				Skill existingSkill = skillRepo.findById(dto.getId())
						.orElseThrow(() -> new InvalidSkillException("Skill not found"));
				if (!existingSkill.getUserProfile().getId().equals(user.getProfile().getId())) {
					throw new InvalidSkillException("Cannot update skill that does not belong to user");
				}
				// Check if name is being changed and if new name already exists
				String newSearchKey = SearchKeyUtil.generate(dto.getName());
				if (!existingSkill.getSearchKey().equals(newSearchKey) &&
						skillRepo.existsByUserProfileAndSearchKey(user.getProfile(), newSearchKey)) {
					throw new SkillAlreadyExistsException("Skill already exists");
				}
				// Update fields
				existingSkill.setName(dto.getName());
				existingSkill.setYearsOfExperience(dto.getYearsOfExperience());
				existingSkill.setProficiencyLevel(dto.getProficiencyLevel());
				existingSkill.setSearchKey(newSearchKey);
				UserProfile profile = user.getProfile();
				profile.setOverallRating(OverAllRating.calculate(profile));
				profileRepo.save(profile);
				skillRepo.save(existingSkill);
				return;
			}
			// Create new skill
			Skill skill = SkillMapper.toSkill(dto);
			if (skillRepo.existsByUserProfileAndSearchKey(
			        user.getProfile(),
			        SearchKeyUtil.generate(skill.getName()))) {

			    throw new SkillAlreadyExistsException("Skill already exists");
			}
			skill.setSearchKey(SearchKeyUtil.generate(skill.getName()));
			skill.setUserProfile(user.getProfile());
			skill.setSkillScore(0.0);
			UserProfile profile=user.getProfile();
			profile.setOverallRating(OverAllRating.calculate(profile));
			profileRepo.save(profile);
			skillRepo.save(skill);
			return;
		}
		throw new InvalidSkillException("Invalid skill exception");
	}

	@Transactional
	public void deleteById(String id, String email) {
		User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not Found"));
		Skill skill = skillRepo.findById(id).orElseThrow(() -> new InvalidSkillException("Skill not found"));
		if (!skill.getUserProfile().getId().equals(user.getProfile().getId())) {
			throw new InvalidSkillException("Cannot delete skill that does not belong to user");
		}
		skillRepo.delete(skill);
	}
	public List<SearchProfileDto> findAllProfiles(String name) {
	    Pageable pageable = PageRequest.of(0, 20, Sort.by("skillScore").descending());
	    Page<Skill> skillsPage = skillRepo
	            .findBySearchKeyContainingIgnoreCaseOrderBySkillScoreDesc(name, pageable);
	    return skillsPage.getContent()
	            .stream()
	            .map(Skill::getUserProfile)
	            .distinct()
	            .map(SearchProfileMapper::toSearchProfileDto)
	            .toList();
	}
	public Set<Skill> findAll(String email) {
		User user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("User not Found"));
		
		return skillRepo.findByUserProfile(user.getProfile());
	}
	
}
